package com.netcracker.service.request;

import com.netcracker.exception.*;
import com.netcracker.exception.IllegalAccessException;
import com.netcracker.exception.request.RequestNotAssignedException;
import com.netcracker.exception.requestGroup.CannotUpdateStatusException;
import com.netcracker.model.dto.FullRequestDTO;
import com.netcracker.model.dto.HistoryDTO;
import com.netcracker.model.dto.Page;
import com.netcracker.model.entity.*;
import com.netcracker.model.event.*;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.interfaces.*;
import com.netcracker.util.ChangeTracker;
import com.netcracker.util.enums.role.RoleEnum;
import com.netcracker.util.enums.status.StatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.netcracker.util.MessageConstant.*;

@Service
public class RequestServiceImpl implements RequestService {

    private final long REMIND_BEFORE_MIN = 86_400_000; // 24 hours
    private final long REMIND_BEFORE_MAX = 172_800_000; // 48 hours

    private ApplicationEventPublisher eventPublisher;

    private final MessageSource messageSource;

    private final RequestRepository requestRepository;

    private final PersonRepository personRepository;

    private final StatusRepository statusRepository;

    private final RoleRepository roleRepository;

    private final PriorityRepository priorityRepository;

    private final ChangeGroupRepository changeGroupRepository;

    private final ChangeItemRepository changeItemRepository;

    private final FieldRepository fieldRepository;

    private final RequestGroupRepository requestGroupRepository;

    private final ChangeTracker changeTracker;

    private final static Logger LOGGER = LoggerFactory.getLogger(RequestServiceImpl.class);

    @Autowired
    public RequestServiceImpl(MessageSource messageSource,
                              RequestRepository requestRepository,
                              PersonRepository personRepository,
                              StatusRepository statusRepository,
                              RoleRepository roleRepository,
                              PriorityRepository priorityRepository,
                              ChangeGroupRepository changeGroupRepository,
                              ChangeItemRepository changeItemRepository,
                              FieldRepository fieldRepository,
                              RequestGroupRepository requestGroupRepository,
                              ChangeTracker changeTracker,
                              ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        this.messageSource = messageSource;
        this.requestRepository = requestRepository;
        this.personRepository = personRepository;
        this.statusRepository = statusRepository;
        this.roleRepository = roleRepository;
        this.priorityRepository = priorityRepository;
        this.changeGroupRepository = changeGroupRepository;
        this.changeItemRepository = changeItemRepository;
        this.fieldRepository = fieldRepository;
        this.requestGroupRepository = requestGroupRepository;
        this.changeTracker = changeTracker;
    }

    @Override
    public Optional<Request> getRequestById(Long id) {
        Optional<Request> request = requestRepository.findOne(id);
        request.ifPresent(this::fillRequest);
        return request;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("isAuthenticated()")
    public Optional<Request> saveRequest(Request request, Principal principal) throws CannotCreateRequestException, CurrentUserNotPresentException {
        Locale locale = LocaleContextHolder.getLocale();
        String email = principal.getName();

        Person manager = checkPersonPresent(personRepository.findPersonByEmail(email)).get();
        request.setEmployee(manager);

        request.setStatus(statusRepository.findStatusByName(StatusEnum.FREE.getName()).orElseThrow(() ->
                new CannotCreateRequestException(messageSource
                        .getMessage(STATUS_ERROR, new Object[]{StatusEnum.FREE.getName()}, locale))
        ));
        request.setCreationTime(new Timestamp(new Date().getTime()));
        Optional<Request> savedRequest = this.requestRepository.save(request);
        //            Automatically subscribe author to request
        personRepository.subscribe(savedRequest.get().getId(), savedRequest.get().getEmployee().getId());
        eventPublisher.publishEvent(new NotificationNewRequestEvent(manager, request));
        return savedRequest;
    }

    /**
     * Update request
     * Only Free and In progress requests can be updated.
     * Admin can update Free and In progress requests.
     * Author can update only Free requests.
     *
     * @param newRequest
     * @param requestId
     * @param principal
     * @return Optional<Request> updated request
     * @throws ResourceNotFoundException
     * @throws IllegalAccessException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("isAuthenticated()")
    public Optional<Request> updateRequest(Request newRequest, Long requestId, Principal principal) throws ResourceNotFoundException, IllegalAccessException {
        Locale locale = LocaleContextHolder.getLocale();

        Request oldRequest = checkRequestPresent(requestRepository.findOne(requestId));
        Optional<Person> employee = checkPersonPresent(personRepository.findOne(newRequest.getEmployee().getId()));
        Optional<Person> currentUser = checkPersonPresent(personRepository.findPersonByEmail(principal.getName()));
        if (StatusEnum.CANCELED.getId().equals(oldRequest.getStatus().getId())){
            throw new IllegalAccessException(messageSource.getMessage(REQUEST_ERROR_UPDATE_CANCELED, null, locale));
        } else if (StatusEnum.CLOSED.getId().equals(oldRequest.getStatus().getId())){
            throw new IllegalAccessException(messageSource.getMessage(REQUEST_ERROR_UPDATE_CLOSED, null, locale));
        } else if (!employee.get().getId().equals(currentUser.get().getId()) && !isCurrentUserAdmin(principal)){
            throw new IllegalAccessException(messageSource.getMessage(REQUEST_ERROR_UPDATE_NOT_PERMISSION, null, locale));
        } else if (!StatusEnum.FREE.getId().equals(oldRequest.getStatus().getId()) && !isCurrentUserAdmin(principal)){
            throw new IllegalAccessException(messageSource.getMessage(REQUEST_ERROR_UPDATE_NON_FREE, null, locale));
        } else {
            eventPublisher.publishEvent(new UpdateRequestEvent(oldRequest, newRequest, new Date(), principal.getName()));
            return this.requestRepository.updateRequest(newRequest);
        }
    }

    @Override
    public Set<ChangeItem> updateRequestHistory(Request newRequest, Request oldRequest, String authorName) throws CurrentUserNotPresentException {
        Optional<Person> author = checkPersonPresent(personRepository.findPersonByEmail(authorName));
        ChangeGroup changeGroup = new ChangeGroup();
        changeGroup.setRequest(new Request(oldRequest.getId()));
        changeGroup.setAuthor(author.get());
        changeGroup.setCreateDate(new Timestamp(System.currentTimeMillis()));
        Set<ChangeItem> changeItemSet = changeTracker.findMismatching(oldRequest, newRequest);
        if (changeItemSet.size() == 0) {
            return null;
        }
        ChangeGroup newChangeGroup = changeGroupRepository.save(changeGroup).get();
        changeItemSet.forEach(ci -> ci.setChangeGroup(new ChangeGroup(newChangeGroup.getId())));
        changeItemSet.forEach(changeItemRepository::save);
        return changeItemSet;
    }

    /**
     * Add request to request group
     * If request already in group - rewrite group
     *
     * @param requestId
     * @param requestGroupId
     * @param principal      Principal of current user
     * @return
     * @throws ResourceNotFoundException
     * @throws IncorrectStatusException
     * @throws IllegalAccessException
     */
    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    public int addToRequestGroup(Long requestId, Integer requestGroupId, Principal principal) throws ResourceNotFoundException, IncorrectStatusException, IllegalAccessException, RequestNotAssignedException {
        Locale locale = LocaleContextHolder.getLocale();

        Request request = checkRequestPresent(requestRepository.findOne(requestId));
        Optional<RequestGroup> requestGroupOptional = requestGroupRepository.findOne(requestGroupId);

        if (!requestGroupOptional.isPresent()) {
            LOGGER.error("Request group with id {} does not exist", requestGroupId);
            throw new ResourceNotFoundException(messageSource
                    .getMessage(REQUEST_GROUP_NOT_EXIST, new Object[]{requestGroupId}, locale));
        }
        if (request.getManager() == null) {
            LOGGER.error(messageSource
                    .getMessage(REQUEST_NOT_ASSIGNED, new Object[]{}, locale));
            throw new RequestNotAssignedException(messageSource
                    .getMessage(REQUEST_NOT_ASSIGNED, new Object[]{}, locale));
        }
        Status status = statusRepository.findOne(request.getStatus().getId()).get();

        if (!status.getName().equalsIgnoreCase(StatusEnum.FREE.getName())) {
            LOGGER.error("Request should be in FREE status for grouping. Current status is {}", status.getName());
            throw new IncorrectStatusException(messageSource.getMessage(STATUS_ERROR_INCORRECT, null, locale),
                    messageSource.getMessage(REQUEST_ERROR_MUST_FREE, new Object[]{status.getName()}, locale));
        }
        if (!isAccessLegal(requestGroupOptional.get(), principal))
            throw new IllegalAccessException(messageSource.getMessage(REQUEST_GROUP_ILLEGAL_ACCESS, null, locale));

        request.setRequestGroup(new RequestGroup(requestGroupId));
        int rowsUpdated = requestRepository.updateRequestGroup(requestId, requestGroupId);
        eventPublisher.publishEvent(new RequestAddToGroupEvent(getRequestById(request.getId()).get()));
        return rowsUpdated;
    }

    /**
     * Remove request from request group
     *
     * @param requestId
     * @param principal
     * @return
     * @throws ResourceNotFoundException
     * @throws IllegalAccessException
     */
    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    public int removeFromRequestGroup(Long requestId, Principal principal) throws ResourceNotFoundException, IllegalAccessException {
        Locale locale = LocaleContextHolder.getLocale();
        Request request = checkRequestPresent(requestRepository.findOne(requestId));
        RequestGroup requestGroup = requestGroupRepository.findOne(request.getRequestGroup().getId()).get();

        if (!isAccessLegal(requestGroup, principal))
            throw new IllegalAccessException(messageSource.getMessage(REQUEST_GROUP_ILLEGAL_ACCESS, null, locale));
        return requestRepository.removeRequestFromRequestGroup(request.getId());
    }

    @Override
    public List<Request> getAllSubRequest(Long parentId) throws ResourceNotFoundException {
        Request parent = checkRequestPresent(requestRepository.findOne(parentId));
        List<Request> requests = requestRepository.getAllSubRequest(parentId);
        requests.forEach(e -> fillRequest(e, parent));
        return requests;
    }

    /**
     * Cancel request
     * Only Free and In progress requests can be canceled.
     * Admin can cancel Free and In progress requests.
     * Author can cancel only Free requests.
     *
     * @param id
     * @param principal
     * @return
     * @throws CannotDeleteRequestException
     * @throws IllegalAccessException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("isAuthenticated()")
    public void deleteRequestById(Long id, Principal principal) throws CannotDeleteRequestException, ResourceNotFoundException, CannotUpdateStatusException {
        Locale locale = LocaleContextHolder.getLocale();
        Optional<Person> currentUser = checkPersonPresent(personRepository.findPersonByEmail(principal.getName()));
        Request request = checkRequestPresent(requestRepository.findOne(id));

        if (StatusEnum.CANCELED.getId().equals(request.getStatus().getId()))
            throw new CannotDeleteRequestException(messageSource.getMessage(REQUEST_ERROR_DELETE_CANCELED, null, locale));
        else if (StatusEnum.CLOSED.getId().equals(request.getStatus().getId()))
            throw new CannotDeleteRequestException(messageSource.getMessage(REQUEST_ERROR_DELETE_CLOSED, null, locale));
        else if (!isCurrentUserAdmin(principal) && !currentUser.get().getId().equals(request.getEmployee().getId()))
            throw new CannotDeleteRequestException(messageSource.getMessage(REQUEST_ERROR_DELETE_NOT_PERMISSION, null, locale));
        else if (!StatusEnum.FREE.getId().equals(request.getStatus().getId()) && !isCurrentUserAdmin(principal))
            throw new CannotDeleteRequestException(messageSource.getMessage(REQUEST_ERROR_DELETE_NOT_FREE, null, locale));
        else {
            changeRequestStatus(request, new Status(StatusEnum.CANCELED.getId()), currentUser.get().getEmail());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("isAuthenticated()")
    public int changeRequestStatus(Request request, Status status, String authorName) throws ResourceNotFoundException, CannotUpdateStatusException {
        Locale locale = LocaleContextHolder.getLocale();
        Request requestDB = checkRequestPresent(requestRepository.findOne(request.getId()));
        Request newRequest = new Request(requestDB);
        if(!StatusEnum.CANCELED.getId().equals(status.getId())){
            if(newRequest.getRequestGroup()!=null){
                throw new CannotUpdateStatusException(messageSource.getMessage(REQUEST_ERROR_UPDATE_STATUS, null, locale));
            }
        }
        newRequest.setStatus(status);
        eventPublisher.publishEvent(new UpdateRequestEvent(requestDB, newRequest, new Date(), authorName));

        if (newRequest.getStatus().getId().equals(StatusEnum.CLOSED.getId())){
            List<Request> subRequestList = getAllSubRequest(newRequest.getId());
            if (!subRequestList.isEmpty()){
                subRequestList.forEach(sub -> {
                    sub.setStatus(new Status(StatusEnum.CLOSED.getId()));
                    requestRepository.updateRequest(sub);
                });
            }
        }
        return requestRepository.changeRequestStatus(request, status);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public Page<HistoryDTO> getRequestHistory(Long requestId, String period, Pageable pageable) {
        try {
            List<ChangeGroup> changeGroups = changeGroupRepository.findByRequestIdWithDetails(requestId,
                    Period.valueOf(period.toUpperCase()), pageable);
            fill(changeGroups);
            List<HistoryDTO> historyList = new ArrayList<>();
            changeGroups.forEach(changeGroup -> historyList.add(new HistoryDTO(changeGroup)));
            Long count = changeGroupRepository.countChangeByRequestId(requestId, Period.valueOf(period.toUpperCase()));
            return new Page<>(pageable.getPageSize(), pageable.getPageNumber(), count, historyList);
        } catch (IllegalArgumentException e) {
            return new Page<>();
        }
    }

    @Override
    public List<Request> getRequestsByRequestGroup(Integer requestGroupId) {
        return requestRepository.findRequestsByRequestGroupId(requestGroupId);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Page<Request> getRequestsByRequestGroup(Integer requestGroupId, Pageable pageable) {
        List<Request> requestsByRequestGroup = requestRepository.findRequestsByRequestGroupId(requestGroupId, pageable);
        requestsByRequestGroup.forEach(this::fillRequest);
        Long count = requestRepository.countRequestsByRequestGroupId(requestGroupId);
        return new Page<>(pageable.getPageSize(), pageable.getPageNumber(), count, requestsByRequestGroup);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Page<FullRequestDTO> getFullRequestDTOByRequestGroup(Integer requestGroupId, Pageable pageable) {
        Page<Request> requestsByRequestGroup = getRequestsByRequestGroup(requestGroupId, pageable);
        List<FullRequestDTO> fullRequestDTOList = requestsByRequestGroup.getData()
                .stream()
                .map(FullRequestDTO::new)
                .collect(Collectors.toList());

        return new Page<>(pageable.getPageSize(), pageable.getPageNumber(),
                requestsByRequestGroup.getTotalElements(), fullRequestDTOList);
    }

    /**
     * This method executes each time specified in property with 'cron' pattern.
     * It is notify manager about request that estimate time will end in 24-48 hours.
     */
    @Scheduled(cron = "${request.expiry.remind.time}")
    @Override
    public void checkRequestsForExpiry() {
        Long currentTime = System.currentTimeMillis();

        List<Request> requests = requestRepository.findAll().stream()
                .filter(r -> r.getEstimate() != null && (r.getStatus().getId() == 1 || r.getStatus().getId() == 2)
                ) // avoid requests without manager and with status closed/canceled
                .filter(r ->
                        {
                            Long difference = r.getEstimate().getTime() - currentTime;

                            return (difference >= REMIND_BEFORE_MIN) &&
                                    (difference < REMIND_BEFORE_MAX);
                        }
                )
                .collect(Collectors.toList());
        requests.forEach(this::fill);
        eventPublisher.publishEvent(new RequestExpiringEvent(requests));
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR')")
    public void unassign(Long requestId, Principal principal) throws CannotUnassignRequestException, ResourceNotFoundException {
        Locale locale = LocaleContextHolder.getLocale();
        Request oldRequest = checkRequestPresent(getRequestById(requestId));

        if(oldRequest.getManager() == null) {
            throw new CannotUnassignRequestException(messageSource.getMessage(REQUEST_NOT_ASSIGNED, null, locale));
        }

        if(oldRequest.getRequestGroup() != null) {
            requestRepository.removeRequestFromRequestGroup(oldRequest.getId());
        }

        requestRepository.unassign(requestId);
        Optional<Request> newRequest = getRequestById(requestId);
        eventPublisher.publishEvent(new RequestUnassignEvent(oldRequest, newRequest.get(), new Date(), principal.getName()));
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean assignRequest(Long requestId, Principal principal) throws CannotAssignRequestException {
        Locale locale = LocaleContextHolder.getLocale();
        Optional<Request> oldRequest = getRequestById(requestId);
        Optional<Person> person = personRepository.findPersonByEmail(principal.getName());

        if (oldRequest.isPresent() && person.isPresent() && oldRequest.get().getManager() == null){
            requestRepository.assignRequest(requestId, person.get().getId(), new Status(1)); // Send status 'FREE', because Office Manager doesn't start do task right now.
            Optional<Request> newRequest = getRequestById(requestId);

            // Subscribe manager to request
            personRepository.subscribe(requestId, person.get().getId());
            eventPublisher.publishEvent(
                    new RequestAssignEvent(oldRequest.get(), newRequest.get(), new Date(), principal.getName()));
            return true;
        }
        throw new CannotAssignRequestException(
                messageSource.getMessage(REQUEST_ERROR_ALREADY_ASSIGNED, null, locale));
    }

    /**
     * Method for assign another person to request
     *
     * @param requestId
     * @param personId
     * @return true in case success operation
     * @throws CannotAssignRequestException when person or request does not exist
     */
    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean assignRequest(Long requestId, Long personId, Principal principal) throws CannotAssignRequestException {
        Locale locale = LocaleContextHolder.getLocale();
        Optional<Request> oldRequest = getRequestById(requestId);
        Optional<Person> person = personRepository.findOne(personId);

        if (oldRequest.isPresent() && person.isPresent()){
            requestRepository.assignRequest(requestId, personId, new Status(1)); // Send status 'FREE', because Office Manager doesn't start do task right now.
            Optional<Request> newRequest = getRequestById(requestId);

            // Subscribe new manager to request
            personRepository.subscribe(requestId, person.get().getId());
            eventPublisher.publishEvent(new RequestAssignEvent(oldRequest.get(), newRequest.get(), new Date(), principal.getName()));

            if(oldRequest.get().getManager() != null) {
                personRepository.unsubscribe(requestId, oldRequest.get().getManager().getId()); // unsubscribe old manager
            }
            return true;
        }
        throw new CannotAssignRequestException(messageSource.getMessage(REQUEST_ERROR_NOT_EXIST_PERSON_OR_REQUEST, null, locale));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Page<Request> getAvailableRequestListByPriority(Integer priorityId, Pageable pageable) {
        Optional<Priority> priority = priorityRepository.findOne(priorityId);
        List<Request> requestList = requestRepository.getFreeRequestsWithPriority(priorityId, pageable, priority.get());
        Long count = requestRepository.countFreeByPriority(priorityId);
        requestList.forEach(this::fillRequest);

        return new Page<>(pageable.getPageSize(), pageable.getPageNumber(), count, requestList);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Page<Request> getAvailableRequestList(Pageable pageable) {
        List<Request> requestList = requestRepository.getFreeRequests(pageable);
        Long count = requestRepository.countFree();
        requestList.forEach(this::fillRequest);

        return new Page<>(pageable.getPageSize(), pageable.getPageNumber(), count, requestList);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Page<Request> getAllRequestByEmployee(Principal principal, Pageable pageable) {
        String employeeEmail = principal.getName();
        Person employee = personRepository.findPersonByEmail(employeeEmail).get();
        List<Request> requestList = requestRepository.getRequestsByEmployee(pageable, employee);
        Long count = requestRepository.countAllRequestByEmployee(employee.getId());
        requestList.forEach(this::fillRequest);

        return new Page<>(pageable.getPageSize(), pageable.getPageNumber(), count, requestList);
    }

    @Override
    @Transactional()
    @PreAuthorize("isAuthenticated()")
    public Page<Request> getClosedRequestByEmployee(Principal principal, Pageable pageable) {
        Person employee = personRepository.findPersonByEmail(principal.getName()).get();
        List<Request> requestList = requestRepository.getClosedRequestsByEmployee(pageable, employee);
        Long count = requestRepository.countClosedRequestByEmployee(employee.getId());
        requestList.forEach(this::fillRequest);
        return new Page<>(pageable.getPageSize(), pageable.getPageNumber(), count, requestList);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR')")
    public Page<Request> getAllRequestByUser(Long userId, Pageable pageable) {
        List<Request> requestList = requestRepository.getAllRequestByUser(userId, pageable);
        Long count = requestRepository.countAllByUser(userId);
        requestList.forEach(this::fillRequest);

        return new Page<>(pageable.getPageSize(), pageable.getPageNumber(), count, requestList);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    public Page<Request> getAllAssignedRequest(Principal principal, Pageable pageable) {
        Person manager = personRepository.findPersonByEmail(principal.getName()).get();
        List<Request> requestList = requestRepository.getAllAssignedRequest(manager.getId(), pageable);
        Long count = requestRepository.countAllAssigned(manager.getId());
        requestList.forEach(this::fillRequest);

        return new Page<>(pageable.getPageSize(), pageable.getPageNumber(), count, requestList);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR')")
    public Page<Request> getAllAssignedRequestByManager(Long managerId, Pageable pageable) {
        List<Request> requestList = requestRepository.getAllAssignedRequestByManager(managerId, pageable);
        Long count = requestRepository.countAllAssignedByManager(managerId);
        requestList.forEach(this::fillRequest);

        return new Page<>(pageable.getPageSize(), pageable.getPageNumber(), count, requestList);
    }

    private void fillRequest(Request request) {
        fill(request);

        Request parent = request.getParent();
        if (parent != null) {
            request.setParent(this.getRequestById(parent.getId()).orElse(null));
        }
    }

    private void fillRequest(Request request, Request parent) {
        fill(request);
        request.setParent(parent);
    }

    public void fill(Request request) {
        Person employee = request.getEmployee();
        if (employee != null) {
            employee = personRepository.findOne(employee.getId()).orElseGet(null);

            Role role = roleRepository.findOne(employee.getRole().getId()).orElseGet(null);
            employee.setRole(role);

            request.setEmployee(employee);
        }

        Person manager = request.getManager();
        if (manager != null) {
            manager = personRepository.findOne(manager.getId()).orElseGet(null);
            request.setManager(manager);
        }

        Priority priority = request.getPriority();
        request.setPriority(priorityRepository.findOne(priority.getId()).orElseGet(null));

        Status status = request.getStatus();
        request.setStatus(statusRepository.findOne(status.getId()).orElseGet(null));

        RequestGroup requestGroup = request.getRequestGroup();
        if (requestGroup != null){
            request.setRequestGroup(requestGroupRepository.findOne(requestGroup.getId()).orElseGet(null));
        }
    }

    private void fill(List<ChangeGroup> changeGroup) {
        changeGroup.forEach(cg -> cg.getChangeItems().forEach(ci -> {
            ci.setField(fieldRepository.findOne(ci.getField().getId()).get());
        }));
    }

    private boolean isAccessLegal(RequestGroup requestGroup, Principal principal) throws CurrentUserNotPresentException, IllegalAccessException {
        Optional<Person> currentUser = checkPersonPresent(personRepository.findPersonByEmail(principal.getName()));
            if (!currentUser.get().getId().equals(requestGroup.getAuthor().getId())) {
            Optional<Role> adminRole = roleRepository.findRoleByName(RoleEnum.ADMINISTRATOR.getName());
            if (!currentUser.get().getRole().getId().equals(adminRole.get().getId())) {
                LOGGER.error("Add to request group can only author or administrator");
                return false;
            }
        }
        return true;
    }

    private boolean isCurrentUserAdmin(Principal principal) throws CurrentUserNotPresentException {
        Optional<Person> currentUser = checkPersonPresent(personRepository.findPersonByEmail(principal.getName()));
        Optional<Role> adminRole = roleRepository.findRoleByName(RoleEnum.ADMINISTRATOR.getName());
        return currentUser.get().getRole().getId().equals(adminRole.get().getId());
    }

    private Optional<Person> checkPersonPresent(Optional<Person> person) throws CurrentUserNotPresentException{
        Locale locale = LocaleContextHolder.getLocale();
        if (!person.isPresent())
            throw new CurrentUserNotPresentException(messageSource.getMessage(USER_ERROR_NOT_PRESENT, null, locale));
        else
            return person;
    }

    private Request checkRequestPresent(Optional<Request> request) throws ResourceNotFoundException{
        Locale locale = LocaleContextHolder.getLocale();
        if (!request.isPresent())
            throw new ResourceNotFoundException(messageSource.getMessage(REQUEST_ERROR_NOT_EXIST, null, locale));
        else
            return request.get();
    }
}
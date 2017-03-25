package com.netcracker.service.request;

import com.netcracker.exception.*;
import com.netcracker.exception.IllegalAccessException;
import com.netcracker.exception.request.RequestNotAssignedException;
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
    public Optional<Request> saveSubRequest(Request subRequest, Principal principal) throws CannotCreateSubRequestException {
        Locale locale = LocaleContextHolder.getLocale();
        String email = principal.getName();

        if (subRequest.getParent() == null) {
            throw new CannotCreateSubRequestException(messageSource
                    .getMessage(SUB_REQUEST_ERROR_PARENT, new Object[]{"null"}, locale));
        }

        Person manager = personRepository.findPersonByEmail(email).orElseThrow(() ->
                new CannotCreateSubRequestException(messageSource
                        .getMessage(MANAGER_ERROR_MAIL, new Object[]{email}, locale)));

        priorityRepository.findOne(subRequest.getPriority().getId()).orElseThrow(() ->
                new CannotCreateSubRequestException(messageSource
                        .getMessage(PRIORITY_ERROR_ID, new Object[]{subRequest.getPriority().getId()}, locale)));

        subRequest.setManager(manager);

        long parentId = subRequest.getParent().getId();
        Request parentRequest = requestRepository.findOne(parentId).orElseThrow(() ->
                new CannotCreateSubRequestException(messageSource
                        .getMessage(SUB_REQUEST_ERROR_PARENT, new Object[]{parentId}, locale)));

        if (parentRequest.getParent() != null) {
            throw new CannotCreateSubRequestException(messageSource
                    .getMessage(SUB_REQUEST_ERROR_PARENT_IS_SUB_REQUEST, null, locale));
        }

        if ((parentRequest.getManager() == null || !Objects.equals(parentRequest.getManager().getId(), manager.getId()))
                && manager.getRole().getId() != 1) {
            throw new CannotCreateSubRequestException(messageSource
                    .getMessage(SUB_REQUEST_ERROR_ILLEGAL_ACCESS, null, locale));
        }

        String parentStatus = parentRequest.getStatus().getName();
        if (StatusEnum.CANCELED.getName().equals(parentStatus) || StatusEnum.CLOSED.getName().equals(parentStatus)) {
            throw new CannotCreateSubRequestException(messageSource
                    .getMessage(SUB_REQUEST_ERROR_PARENT_CLOSED, null, locale));
        }

        subRequest.setCreationTime(new Timestamp(System.currentTimeMillis()));
        subRequest.setEmployee(parentRequest.getEmployee());
        subRequest.setStatus(statusRepository.findStatusByName(StatusEnum.FREE.getName()).orElseThrow(() ->
                new CannotCreateSubRequestException(messageSource
                        .getMessage(STATUS_ERROR, new Object[]{StatusEnum.FREE.getName()}, locale))));
        return requestRepository.save(subRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    //@PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE', 'ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    public Optional<Request> saveRequest(Request request, Principal principal) throws CannotCreateRequestException {
        Locale locale = LocaleContextHolder.getLocale();
        String email = principal.getName();

        Person manager = personRepository.findPersonByEmail(email).orElseThrow(() ->
                new CannotCreateRequestException(messageSource
                        .getMessage(EMPLOYEE_ERROR_MAIL, new Object[]{email}, locale)));

        request.setEmployee(manager);

        request.setStatus(statusRepository.findStatusByName(StatusEnum.FREE.getName()).orElseThrow(() ->
                new CannotCreateRequestException(messageSource
                        .getMessage(STATUS_ERROR, new Object[]{StatusEnum.FREE.getName()}, locale))
        ));
        request.setCreationTime(new Timestamp(new Date().getTime()));
        eventPublisher.publishEvent(new NotificationNewRequestEvent(manager));
        Optional<Request> savedRequest = this.requestRepository.save(request);
        //            Automatically subscribe author to request
        personRepository.subscribe(savedRequest.get().getId(), savedRequest.get().getEmployee().getId());
        return savedRequest;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE', 'ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    public Optional<Request> updateRequest(Request newRequest, Long requestId, Principal principal) throws ResourceNotFoundException, IllegalAccessException {
        Locale locale = LocaleContextHolder.getLocale();

        Optional<Request> oldRequest = requestRepository.findOne(requestId);
        Optional<Person> employee = personRepository.findOne(newRequest.getEmployee().getId());
        Optional<Person> currentUser = personRepository.findPersonByEmail(principal.getName());

        if (!oldRequest.isPresent()) return Optional.empty();
        if (StatusEnum.CANCELED.getId().equals(oldRequest.get().getStatus().getId())){
            throw new IllegalAccessException(messageSource.getMessage(REQUEST_ERROR_UPDATE_CANCELED, null, locale));
        } else if (StatusEnum.CLOSED.getId().equals(oldRequest.get().getStatus().getId())){
            throw new IllegalAccessException(messageSource.getMessage(REQUEST_ERROR_UPDATE_CLOSED, null, locale));
        } else if (!employee.get().getId().equals(currentUser.get().getId()) && !isCurrentUserAdmin(principal)){
            throw new IllegalAccessException(messageSource.getMessage(REQUEST_ERROR_UPDATE_NOT_PERMISSION, null, locale));
        } else if (!StatusEnum.FREE.getId().equals(oldRequest.get().getStatus().getId()) && !isCurrentUserAdmin(principal)){
            throw new IllegalAccessException(messageSource.getMessage(REQUEST_ERROR_UPDATE_NON_FREE, null, locale));
        } else {
            eventPublisher.publishEvent(new ChangeRequestEvent(oldRequest.get(), newRequest, new Date()));
            eventPublisher.publishEvent(new NotificationRequestUpdateEvent(employee.get(), new Request(oldRequest.get().getId())));
            updateRequestHistory(newRequest, oldRequest.get(), principal.getName());
            return this.requestRepository.updateRequest(newRequest);
        }
    }


    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE', 'ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    public Optional<Request> updateRequestPriority(Long requestId, String priority, Principal principal) {
        String authorName = principal.getName();
        Optional<Request> futureNewRequest = requestRepository.findOne(requestId);
        if (!futureNewRequest.isPresent()) return Optional.empty();
        Optional<Priority> p = priorityRepository.findPriorityByName(priority);
        if (!p.isPresent()) return Optional.empty();
        Request oldRequest = new Request(futureNewRequest.get());
        futureNewRequest.get().setPriority(p.get());
        updateRequestHistory(futureNewRequest.get(), oldRequest, authorName);

        eventPublisher.publishEvent(new ChangeRequestEvent(oldRequest, futureNewRequest.get(), new Date()));

        this.requestRepository.updateRequestPriority(futureNewRequest.get());
        return futureNewRequest;
    }


    private Optional<Request> updateRequestHistory(Request newRequest, Request oldRequest, String authorName) {
        Optional<Person> author = personRepository.findPersonByEmail(authorName);
        if (!author.isPresent()) return Optional.empty();
        ChangeGroup changeGroup = new ChangeGroup();
        changeGroup.setRequest(new Request(oldRequest.getId()));
        changeGroup.setAuthor(author.get());
        changeGroup.setCreateDate(new Timestamp(System.currentTimeMillis()));
        Set<ChangeItem> changeItemSet = changeTracker.findMismatching(oldRequest, newRequest);
        if (changeItemSet.size() == 0) {
            return Optional.empty();
        }
        ChangeGroup newChangeGroup = changeGroupRepository.save(changeGroup).get();
        changeItemSet.forEach(ci -> ci.setChangeGroup(new ChangeGroup(newChangeGroup.getId())));
        changeItemSet.forEach(changeItemRepository::save);
        return Optional.of(newRequest);
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

        Optional<Request> requestOptional = requestRepository.findOne(requestId);

        if (!requestOptional.isPresent()) {
            LOGGER.error("Request with id {} does not exist", requestId);
            throw new ResourceNotFoundException(messageSource
                    .getMessage(REQUEST_ERROR_NOT_EXIST, new Object[]{requestId}, locale));
        }

        Optional<RequestGroup> requestGroupOptional = requestGroupRepository.findOne(requestGroupId);

        if (!requestGroupOptional.isPresent()) {
            LOGGER.error("Request group with id {} does not exist", requestGroupId);
            throw new ResourceNotFoundException(messageSource
                    .getMessage(REQUEST_GROUP_NOT_EXIST, new Object[]{requestGroupId}, locale));
        }

        Request request = requestOptional.get();

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
        return requestRepository.updateRequestGroup(requestId, requestGroupId);
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

        Optional<Request> requestOptional = requestRepository.findOne(requestId);

        if (!requestOptional.isPresent()) {
            LOGGER.error("Request with id {} not exist", requestId);
            throw new ResourceNotFoundException(messageSource.getMessage(REQUEST_ERROR_NOT_EXIST, new Object[]{requestId}, locale));
        }

        Request request = requestOptional.get();

        RequestGroup requestGroup = requestGroupRepository.findOne(request.getRequestGroup().getId()).get();

        if (!isAccessLegal(requestGroup, principal))
            throw new IllegalAccessException(messageSource.getMessage(REQUEST_GROUP_ILLEGAL_ACCESS, null, locale));

        return requestRepository.removeRequestFromRequestGroup(request.getId());
    }

    @Override
    public List<Request> getAllSubRequest(Long parentId) throws ResourceNotFoundException {
        Locale locale = LocaleContextHolder.getLocale();

        Request parent = requestRepository.findOne(parentId).orElseThrow(() ->
                new ResourceNotFoundException(messageSource.getMessage(SUB_REQUEST_ERROR_PARENT, new Object[]{parentId}, locale)));

        List<Request> requests = requestRepository.getAllSubRequest(parentId);
        requests.forEach(e -> fillRequest(e, parent));
        return requests;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE', 'ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    public void deleteRequestById(Long id, Principal principal) throws CannotDeleteRequestException, ResourceNotFoundException {
        Locale locale = LocaleContextHolder.getLocale();

        Optional<Person> currentUser = personRepository.findPersonByEmail(principal.getName());
        Optional<Request> requestOptional = requestRepository.findOne(id);
        if (!requestOptional.isPresent())
            throw new ResourceNotFoundException(messageSource.getMessage(REQUEST_ERROR_NOT_EXIST, new Object[]{id}, locale));
        Request request = requestOptional.get();

        if (StatusEnum.CANCELED.getId().equals(request.getStatus().getId()))
            throw new CannotDeleteRequestException(messageSource.getMessage(REQUEST_ERROR_DELETE_CANCELED, null, locale));
        else if (StatusEnum.CLOSED.getId().equals(request.getStatus().getId()))
            throw new CannotDeleteRequestException(messageSource.getMessage(REQUEST_ERROR_DELETE_CLOSED, null, locale));
        else if (!isCurrentUserAdmin(principal) && !currentUser.get().getId().equals(request.getEmployee().getId()))
            throw new CannotDeleteRequestException(messageSource.getMessage(REQUEST_ERROR_DELETE_NOT_PERMISSION, null, locale));
        else if (!StatusEnum.FREE.getId().equals(request.getStatus().getId()) && !isCurrentUserAdmin(principal))
            throw new CannotDeleteRequestException(messageSource.getMessage(REQUEST_ERROR_DELETE_NOT_FREE, null, locale));
        else {
            requestRepository.deleteRequest(request);
            if (request.getParent() == null) {
                List<Request> subRequestList = getAllSubRequest(request.getId());
                if (!subRequestList.isEmpty())
                    subRequestList.forEach(r -> requestRepository.deleteRequest(r));
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE', 'ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    public int changeRequestStatus(Request request, Status status, String authorName) {
        Optional<Request> requestDB = requestRepository.findOne(request.getId());
        Optional<Person> person = personRepository.findOne(requestDB.get().getEmployee().getId());
        Request newRequest = new Request(requestDB.get());

        newRequest.setStatus(status);
        updateRequestHistory(newRequest, requestDB.get(), authorName);
        eventPublisher.publishEvent(new NotificationChangeStatus(person.get(), new Request(newRequest.getId())));
        eventPublisher.publishEvent(new ChangeRequestEvent(requestDB.get(), newRequest, new Date()));
        return requestRepository.changeRequestStatus(request, status);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public Set<ChangeGroup> getRequestHistory(Long requestId, String period, Pageable pageable) {
        try {
            Set<ChangeGroup> changeGroups = changeGroupRepository.findByRequestIdWithDetails(requestId,
                    Period.valueOf(period.toUpperCase()), pageable);
            fill(changeGroups);
            return changeGroups;
        } catch (IllegalArgumentException e) {
            return new HashSet<ChangeGroup>();
        }
    }

    @Override
    public List<Request> getRequestsByRequestGroup(Integer requestGroupId) {
        return requestRepository.findRequestsByRequestGroupId(requestGroupId);
    }

    @Override
    public List<Request> getRequestsByRequestGroup(Integer requestGroupId, Pageable pageable) {
        List<Request> requestsByRequestGroupId = requestRepository.findRequestsByRequestGroupId(requestGroupId, pageable);
        requestsByRequestGroupId.forEach(this::fillRequest);
        return requestsByRequestGroupId;
    }

    @Scheduled(cron = "${request.expiry.remind.time}")
    @Override
    public void checkRequestsForExpiry() {
        Long currentTime = System.currentTimeMillis();

        List<Request> requests = requestRepository.findAll().stream()
                .filter(r -> r.getEstimate() != null &&
                        (r.getStatus().getId() == 1 || r.getStatus().getId() == 2)) // avoid requests without manager and closed/canceled
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
    @PreAuthorize("hasAnyAuthority('ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean assignRequest(Long requestId, Principal principal) throws CannotAssignRequestException {
        Locale locale = LocaleContextHolder.getLocale();
        Optional<Request> request = getRequestById(requestId);
        Optional<Person> person = personRepository.findPersonByEmail(principal.getName());

        if (request.isPresent() && person.isPresent() && request.get().getManager() == null){
            requestRepository.assignRequest(requestId, person.get().getId(), new Status(1)); // Send status 'FREE', because Office Manager doesn't start do task right now.
//            Automatically subscribe manager to request
            personRepository.subscribe(requestId, person.get().getId());
            return true;
        }

        throw new CannotAssignRequestException(messageSource.getMessage(REQUEST_ERROR_ALREADY_ASSIGNED, null, locale));
    }

    /**
     * Method for assign another person to request
     * @param requestId
     * @param personId
     * @return true in case success operation
     * @throws CannotAssignRequestException
     */
    @Override
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATOR')")
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean assignRequest(Long requestId, Long personId) throws CannotAssignRequestException {
        Locale locale = LocaleContextHolder.getLocale();
        Optional<Request> request = getRequestById(requestId);
        Optional<Request> person = getRequestById(personId);

        if (request.isPresent() && person.isPresent()){
            requestRepository.assignRequest(requestId, personId, new Status(1)); // Send status 'FREE', because Office Manager doesn't start do task right now.
            return true;
        }

        throw new CannotAssignRequestException(messageSource.getMessage(REQUEST_ERROR_NOT_EXIST_PERSON_OR_REQUEST, null, locale));
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE', 'ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    public Page<Request> getAvailableRequestListByPriority(Integer priorityId, Pageable pageable) {
        Optional<Priority> priority = priorityRepository.findOne(priorityId);
        List<Request> requestList = requestRepository.getFreeRequestsWithPriority(priorityId, pageable, priority.get());

        Long count = requestRepository.countFreeByPriority(priorityId);
        requestList.forEach(this::fillRequest);

        return new Page<>(pageable.getPageSize(), pageable.getPageNumber(), count, requestList);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    public Page<Request> getAvailableRequestList(Pageable pageable) {
        List<Request> requestList = requestRepository.getFreeRequests(pageable);

        Long count = requestRepository.countFree();
        requestList.forEach(this::fillRequest);

        return new Page<>(pageable.getPageSize(), pageable.getPageNumber(), count, requestList);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE', 'ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    public Page<Request> getAllRequestByEmployee(Principal principal, Pageable pageable) {
        String employeeEmail = principal.getName();
        Person employee = personRepository.findPersonByEmail(employeeEmail).get();
        List<Request> requestList = requestRepository.getRequestsByEmployee(pageable, employee);
        Long count = requestRepository.countAllRequestByEmployee(employee.getId());
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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR')")
    public Page<Request> getAllAssignedRequestByManager(Long managerId, Pageable pageable) {
        List<Request> requestList = requestRepository.getAllAssignedRequest(managerId, pageable);
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
    }

    private void fill(Set<ChangeGroup> changeGroup) {
        changeGroup.forEach(cg -> cg.getChangeItems().forEach(ci -> {
            ci.setField(fieldRepository.findOne(ci.getField().getId()).get());
        }));
    }

    private boolean isAccessLegal(RequestGroup requestGroup, Principal principal) throws CurrentUserNotPresentException, IllegalAccessException {
        Locale locale = LocaleContextHolder.getLocale();

        Optional<Person> currentUser = personRepository.findPersonByEmail(principal.getName());
        if (!currentUser.isPresent()) {
            LOGGER.warn("Current user not present");
            throw new CurrentUserNotPresentException(messageSource.getMessage(USER_ERROR_NOT_PRESENT, null, locale));
        } else if (!currentUser.get().getId().equals(requestGroup.getAuthor().getId())) {
            Optional<Role> adminRole = roleRepository.findRoleByName(RoleEnum.ADMINISTRATOR.getName());
            if (!currentUser.get().getRole().getId().equals(adminRole.get().getId())) {
                LOGGER.error("Add to request group can only author or administrator");
                return false;
            }
        }

        return true;
    }

    private boolean isCurrentUserAdmin(Principal principal) throws CurrentUserNotPresentException {
        Locale locale = LocaleContextHolder.getLocale();
        Optional<Person> currentUser = personRepository.findPersonByEmail(principal.getName());
        if (!currentUser.isPresent())
            throw new CurrentUserNotPresentException(messageSource.getMessage(USER_ERROR_NOT_PRESENT, null, locale));
        else {
            Optional<Role> adminRole = roleRepository.findRoleByName(RoleEnum.ADMINISTRATOR.getName());
            if (!currentUser.get().getRole().getId().equals(adminRole.get().getId())) {
                return false;
            } else
                return true;
        }
    }
}
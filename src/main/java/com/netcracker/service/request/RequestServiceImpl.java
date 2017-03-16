package com.netcracker.service.request;

import com.netcracker.exception.*;
import com.netcracker.exception.IllegalAccessException;
import com.netcracker.model.entity.*;
import com.netcracker.model.event.NotificationChangeStatus;
import com.netcracker.model.event.NotificationNewRequestEvent;
import com.netcracker.model.event.NotificationRequestUpdateEvent;
import com.netcracker.model.event.RequestExpiringEvent;
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

    private static final long REMIND_TIME_BEFORE_EXPIRY = 3_600_000; // 1 hour

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
    public Optional<Request> saveSubRequest(Request subRequest, String email) throws CannotCreateSubRequestException {
        Locale locale = LocaleContextHolder.getLocale();
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
    public Optional<Request> saveRequest(Request request, String email) throws CannotCreateRequestException {
        Locale locale = LocaleContextHolder.getLocale();

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
        return this.requestRepository.save(request);
    }

    @Override
    public Optional<Request> updateRequest(Request request, Long requestId, Principal principal) throws ResourceNotFoundException, IllegalAccessException {
        Locale locale = LocaleContextHolder.getLocale();

        Optional<Request> oldRequest = requestRepository.findOne(requestId);
        Optional<Person> person = personRepository.findOne(request.getEmployee().getId());
        Optional<Person> currentUser = personRepository.findPersonByEmail(principal.getName());
        if(!oldRequest.isPresent()) return Optional.empty();
        if (isCurrentUserAdmin(principal)){
            eventPublisher.publishEvent(new NotificationRequestUpdateEvent(person.get()));
            updateRequestHistory(request, oldRequest.get(), principal.getName());
            return this.requestRepository.updateRequest(request);
        } else if (!person.get().getId().equals(currentUser.get().getId())){
            throw new IllegalAccessException(messageSource.getMessage(REQUEST_ERROR_UPDATE_NOT_PERMISSION, null, locale));
        } else if (oldRequest.get().getStatus().getId()!=StatusEnum.FREE.getId()){
            throw new IllegalAccessException(messageSource.getMessage(REQUEST_ERROR_UPDATE_NON_FREE, null, locale));
        } else {
            eventPublisher.publishEvent(new NotificationRequestUpdateEvent(person.get()));
            updateRequestHistory(request, oldRequest.get(), principal.getName());
            return this.requestRepository.updateRequest(request);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Optional<Request> updateRequestPriority(Long requestId, String priority, String authorName) {
        Optional<Request> futureNewRequest = requestRepository.findOne(requestId);
        if (!futureNewRequest.isPresent()) return Optional.empty();
        Optional<Priority> p = priorityRepository.findPriorityByName(priority);
        if (!p.isPresent()) return Optional.empty();
        Request oldRequest = new Request(futureNewRequest.get());
        futureNewRequest.get().setPriority(p.get());
        updateRequestHistory(futureNewRequest.get(), oldRequest, authorName);
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
    public int addToRequestGroup(Long requestId, Integer requestGroupId, Principal principal) throws ResourceNotFoundException, IncorrectStatusException, IllegalAccessException {
        Locale locale = LocaleContextHolder.getLocale();

        LOGGER.trace("Getting request with id {} from database", requestId);
        Optional<Request> requestOptional = requestRepository.findOne(requestId);

        if (!requestOptional.isPresent()) {
            LOGGER.error("Request with id {} does not exist", requestId);
            throw new ResourceNotFoundException(messageSource
                    .getMessage(REQUEST_ERROR_NOT_EXIST, new Object[]{requestId}, locale));
        }

        LOGGER.trace("Getting request group with id {} from database");
        Optional<RequestGroup> requestGroupOptional = requestGroupRepository.findOne(requestGroupId);

        if (!requestGroupOptional.isPresent()) {
            LOGGER.error("Request group with id {} does not exist", requestGroupId);
            throw new ResourceNotFoundException(messageSource
                    .getMessage(REQUEST_GROUP_NOT_EXIST, new Object[]{requestGroupId}, locale));
        }

        Request request = requestOptional.get();

        LOGGER.trace("Getting status with id {} from database", request.getStatus().getId());
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
    public int removeFromRequestGroup(Long requestId, Principal principal) throws ResourceNotFoundException, IllegalAccessException {
        Locale locale = LocaleContextHolder.getLocale();

        LOGGER.trace("Get request with id {} from database", requestId);
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
    public void deleteRequestById(Long id, Principal principal) throws CannotDeleteRequestException, ResourceNotFoundException {
        Locale locale = LocaleContextHolder.getLocale();

        Request request = getRequestById(id).get();
        Optional<Person> currentUser = personRepository.findPersonByEmail(principal.getName());
        if (StatusEnum.CLOSED.getName().equals(request.getStatus().getName()))
            throw new CannotDeleteRequestException(messageSource.getMessage(REQUEST_ERROR_DELETE_CLOSED, null, locale));
        else if (!isCurrentUserAdmin(principal) && !currentUser.get().getId().equals(request.getEmployee().getId()))
            throw new CannotDeleteRequestException(messageSource.getMessage(REQUEST_ERROR_DELETE_NOT_PERMISSION, null, locale));
        else if (!StatusEnum.FREE.getId().equals(request.getStatus().getId()) && !isCurrentUserAdmin(principal))
            throw new CannotDeleteRequestException(messageSource.getMessage(REQUEST_ERROR_DELETE_NOT_FREE, null, locale));
        else {
            changeRequestStatus(request, new Status(StatusEnum.CANCELED.getId()));
            if (request.getParent()==null) {
                List<Request> subRequestList = getAllSubRequest(request.getId());
                if (!subRequestList.isEmpty())
                    subRequestList.forEach(r -> changeRequestStatus(r, new Status(StatusEnum.CANCELED.getId())));
            }
        }
    }

    @Override
    public int changeRequestStatus(Request request, Status status) {
        if (request.getEmployee() != null) {
            Optional<Person> person = personRepository.findOne(request.getEmployee().getId());
            eventPublisher.publishEvent(new NotificationChangeStatus(person.get()));
            return requestRepository.changeRequestStatus(request, status);
        }
        Optional<Request> requestDB = requestRepository.findOne(request.getId());
        Optional<Person> person = personRepository.findOne(requestDB.get().getEmployee().getId());
        eventPublisher.publishEvent(new NotificationChangeStatus(person.get()));
        return requestRepository.changeRequestStatus(request, status);
    }

    @Override
    @Transactional(readOnly = true)
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

    @Scheduled(fixedRate = REMIND_TIME_BEFORE_EXPIRY)
    @Override
    public void checkRequestsForExpiry() {
        Long currentTime = System.currentTimeMillis();

        List<Request> requests = requestRepository.findAll().stream()
                .filter(r -> r.getEstimate() != null)
                .filter(r ->
                        {
                            Long difference = r.getEstimate().getTime() - currentTime;
                            return (difference >= 0) &&
                                    (difference < REMIND_TIME_BEFORE_EXPIRY);
                        }
                )
                .collect(Collectors.toList());
        requests.forEach(this::fill);

        eventPublisher.publishEvent(new RequestExpiringEvent(requests));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean assignRequest(Long requestId, Long personId, Principal principal) throws CannotAssignRequestException {
        Locale locale = LocaleContextHolder.getLocale();
        Request request = getRequestById(requestId).get();
        Optional<Person> person = Optional.ofNullable(personRepository.findOne(personId)
                .orElse((personRepository.findPersonByEmail(principal.getName()).get())));

        if (request.getManager() == null) {
            requestRepository.assignRequest(requestId, person.get().getId(), new Status(1)); // Send status 'FREE', because Office Manager doesn't start do task right now.
            return true;
        }

        throw new CannotAssignRequestException(messageSource.getMessage(REQUEST_ERROR_ALREADY_ASSIGNED, null, locale));
    }

    @Override
    public List<Request> getAvailableRequestList(Integer priorityId, Pageable pageable) {
        Optional<Priority> priority = priorityRepository.findOne(priorityId);
        List<Request> requestList = requestRepository.getRequests(priorityId, pageable, priority);

        requestList.forEach(this::fillRequest);

        return requestList;

    }

    @Override
    public List<Request> getAllRequestByEmployee(String employeeEmail, Pageable pageable) {
        Person employee = personRepository.findPersonByEmail(employeeEmail).get();
        List<Request> requestList = requestRepository.getRequestsByEmployee(pageable, employee);

        requestList.forEach(this::fillRequest);

        return requestList;
    }

    @Override
    public Long getCountFree(Integer priorityId) {
        return requestRepository.countFree(priorityId);
    }

    @Override
    public Long getCountAllRequestByEmployee(String employeeEmail) {
        Person employee = personRepository.findPersonByEmail(employeeEmail).get();
        return requestRepository.countAllRequestByEmployee(employee.getId());
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

    private void fill(Request request) {
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

package com.netcracker.service.request;

import com.netcracker.exception.*;
import com.netcracker.model.entity.*;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.impl.RequestRepositoryImpl;
import com.netcracker.repository.data.interfaces.*;
import com.netcracker.util.enums.status.StatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.*;

@Service
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final PersonRepository personRepository;

    private final StatusRepository statusRepository;

    private final RoleRepository roleRepository;

    private final PriorityRepository priorityRepository;

    private final ChangeGroupRepository changeGroupRepository;

    private final FieldRepository fieldRepository;

    private final RequestGroupRepository requestGroupRepository;

    private final static Logger LOGGER = LoggerFactory.getLogger(RequestServiceImpl.class);

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository,
                              PersonRepository personRepository,
                              StatusRepository statusRepository,
                              RoleRepository roleRepository,
                              PriorityRepository priorityRepository,
                              ChangeGroupRepository changeGroupRepository,
                              FieldRepository fieldRepository,
                              RequestGroupRepository requestGroupRepository) {
        this.requestRepository = requestRepository;
        this.personRepository = personRepository;
        this.statusRepository = statusRepository;
        this.roleRepository = roleRepository;
        this.priorityRepository = priorityRepository;
        this.changeGroupRepository = changeGroupRepository;
        this.fieldRepository = fieldRepository;
        this.requestGroupRepository = requestGroupRepository;
    }

    @Override
    public Optional<Request> getRequestById(Long id) {
        Optional<Request> request = requestRepository.findOne(id);
        request.ifPresent(this::fillRequest);
        return request;
    }

    @Override
    public Optional<Request> saveSubRequest(Request subRequest, String email) throws CannotCreateSubRequestException {
        if (subRequest.getParent() == null) {
            throw new CannotCreateSubRequestException("No parent request");
        }

        Person manager = personRepository.findPersonByEmail(email).orElseThrow(() ->
                new CannotCreateSubRequestException("No manager with email " + email));

        subRequest.setManager(manager);

        Request parentRequest = requestRepository.findOne(subRequest.getParent().getId()).orElseThrow(() ->
                new CannotCreateSubRequestException("No parent request with id " + subRequest.getParent().getId()));

        if (parentRequest.getParent() != null) {
            throw new CannotCreateSubRequestException("Parent request is sub request");
        }

        String parentStatus = parentRequest.getStatus().getName();
        if ("CANCELED".equals(parentStatus) || "CLOSED".equals(parentStatus)) {
            throw new CannotCreateSubRequestException("Parent request is closed or canceled");
        }

        subRequest.setCreationTime(new Timestamp(System.currentTimeMillis()));
        subRequest.setEmployee(parentRequest.getEmployee());
        subRequest.setPriority(parentRequest.getPriority());
        subRequest.setStatus(statusRepository.findStatusByName("IN PROGRESS").orElseThrow(() ->
                new CannotCreateSubRequestException("No status 'IN PROGRESS'")));
        return requestRepository.save(subRequest);
    }

    @Override
    public Optional<Request> saveRequest(Request request, String email) throws CannotCreateRequestException {
        Person manager = personRepository.findPersonByEmail(email).orElseThrow(() ->
                new CannotCreateRequestException("No employee with email " + email));

        request.setEmployee(manager);

        request.setStatus(statusRepository.findStatusByName("FREE").orElseThrow(() ->
                new CannotCreateRequestException("No status 'FREE'")
        ));
        request.setCreationTime(new Timestamp(new Date().getTime()));
        return this.requestRepository.save(request);
    }

    @Override
    public Optional<Request> updateRequest(Request request, Long requestId) {
        request.setId(requestId);
        return this.requestRepository.updateRequest(request);
    }

    @Override
    public Optional<Request> addToRequestGroup(Long requestId, Integer requestGroupId) throws ResourceNotFoundException, IncorrectStatusException {
        LOGGER.trace("Get request with id {} from database", requestId);
        Optional<Request> requestOptional = requestRepository.findOne(requestId);

        if (!requestOptional.isPresent()) {
            LOGGER.error("Request with id {} not exist", requestId);
            throw new ResourceNotFoundException("Request with id " + requestId + " not exist");
        }

        LOGGER.trace("Get request group with id {} from database");
        Optional<RequestGroup> requestGroupOptional = requestGroupRepository.findOne(requestGroupId);

        if (!requestGroupOptional.isPresent()) {
            LOGGER.error("Request group with id {} not exist", requestGroupId);
            throw new ResourceNotFoundException("Request group with id " + requestGroupId + " not exist");
        }

        Request request = requestOptional.get();

        LOGGER.trace("Get status with id {} from database", request.getStatus().getId());
        Status status = statusRepository.findOne(request.getStatus().getId()).get();

        if (!status.getName().equalsIgnoreCase(StatusEnum.FREE.toString())) {
            LOGGER.error("Request should be in FREE status for grouping. Current status is {}", status.getName());
            throw new IncorrectStatusException("Incorrect status",
                    "Request should be in FREE status for grouping. Current status is " + status.getName());
        }

        request.setRequestGroup(new RequestGroup(requestGroupId));
        // TODO: 07.03.2017 Add method save in service ?
        return requestRepository.save(request);
    }

    @Override
    public Optional<Request> removeFromRequestGroup(Long requestId) throws ResourceNotFoundException {
        LOGGER.trace("Get request with id {} from database", requestId);
        Optional<Request> requestOptional = requestRepository.findOne(requestId);

        if (!requestOptional.isPresent()) {
            LOGGER.error("Request with id {} not exist", requestId);
            throw new ResourceNotFoundException("Request with id " + requestId + " not exist");
        }

        Request request = requestOptional.get();
        request.setRequestGroup(null);
        return requestRepository.save(request);
    }

    @Override
    public List<Request> getAllSubRequest(Long parentId) throws ResourceNotFoundException {
        Request parent = requestRepository.findOne(parentId).orElseThrow(() ->
                new ResourceNotFoundException("No such parent request"));

        List<Request> requests = requestRepository.getAllSubRequest(parentId);
        requests.forEach(e -> fillRequest(e, parent));
        return requests;
    }

    @Override
    public void deleteRequestById(Long id) throws CannotDeleteRequestException, ResourceNotFoundException {
        Request request = getRequestById(id).get();
        if (request.getStatus().getId().equals(3))   // if request closed
            throw new CannotDeleteRequestException("You cannot delete closed request");
        else {
            changeRequestStatus(request, new Status(5));
            if (request.getParent()==null) {
                List<Request> subRequestList = getAllSubRequest(request.getId());
                if (!subRequestList.isEmpty()) {
                    for (Request r : subRequestList) {
                        changeRequestStatus(r, new Status(5));
                    }
                }
            }
        }
    }

    @Override
    public int changeRequestStatus(Request request, Status status) {
        return requestRepository.changeRequestStatus(request, status);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<ChangeGroup> getRequestHistory(Long requestId, String period) {
        try {
            Set<ChangeGroup> changeGroups = changeGroupRepository.findByRequestIdWithDetails(requestId, Period.valueOf(period.toUpperCase()));
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
        return requestRepository.findRequestsByRequestGroupId(requestGroupId, pageable);
    }

    @Override
    public boolean assignRequest(Long requestId, Long personId, Principal principal) throws CannotAssignRequestException {
        Request request = getRequestById(requestId).get();
        Optional<Person> person = Optional.ofNullable(personRepository.findOne(personId)
                .orElse((personRepository.findPersonByEmail(principal.getName()).get())));

        if (request.getManager() == null) {
            requestRepository.assignRequest(requestId, person.get().getId(), new Status(1)); // Send status 'FREE', because Office Manager doesn't start do task right now.
            return true;
        }

        throw new CannotAssignRequestException("Request is already assigned");
    }

    @Override
    public List<Request> getAvailableRequestList(Integer priorityId, Pageable pageable) {
        Optional<Priority> priority = priorityRepository.findOne(priorityId);
        List<Request> requestList = priority.isPresent() ? requestRepository.queryForList(
                RequestRepositoryImpl.GET_AVAILABLE_REQUESTS_BY_PRIORITY, pageable, priorityId)
                : requestRepository.queryForList(RequestRepositoryImpl.GET_AVAILABLE_REQUESTS, pageable);

        requestList.forEach(this::fillRequest);

        return requestList;

    }

    @Override
    public Long getCountFree(Integer priorityId) {
        return requestRepository.countFree(priorityId);
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
}

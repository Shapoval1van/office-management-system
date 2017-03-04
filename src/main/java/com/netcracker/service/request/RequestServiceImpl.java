package com.netcracker.service.request;

import com.netcracker.exception.*;
import com.netcracker.model.entity.*;
import com.netcracker.repository.data.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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


    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository,
                              PersonRepository personRepository,
                              StatusRepository statusRepository,
                              RoleRepository roleRepository,
                              PriorityRepository priorityRepository,
                              ChangeGroupRepository changeGroupRepository,
                              FieldRepository fieldRepository) {
        this.requestRepository = requestRepository;
        this.personRepository = personRepository;
        this.statusRepository = statusRepository;
        this.roleRepository = roleRepository;
        this.priorityRepository = priorityRepository;
        this.changeGroupRepository = changeGroupRepository;
        this.fieldRepository = fieldRepository;
    }

    @Override
    public Optional<Request> getRequestById(Long id) {
        Optional<Request> request = requestRepository.findOne(id);
        request.ifPresent(this::fillRequest);
        return request;
    }

    @Transactional(propagation = Propagation.REQUIRED)
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
    public List<Request> getAllSubRequest(Long parentId) throws ResourceNotFoundException {
        Request parent = requestRepository.findOne(parentId).orElseThrow(() ->
                new ResourceNotFoundException("No such parent request"));

        List<Request> requests = requestRepository.getAllSubRequest(parentId);
        requests.forEach(e -> fillRequest(e, parent));
        return requests;
    }

    @Transactional
    @Override
    public void deleteRequestById(Long id) throws CannotDeleteRequestException, ResourceNotFoundException {
        Optional<Request> requestOptional = getRequestById(id);
        if(!requestOptional.isPresent()) {
            throw new CannotDeleteRequestException("No such request id " + id);
        }
        Request request = requestOptional.get();
        if (request.getParent() != null) {
            this.requestRepository.delete(id);
        } else {
            if (!request.getStatus().getId().equals(3)) {  // if request not closed
                changeRequestStatus(request, new Status(5));
                List<Request> subRequestList = getAllSubRequest(request.getId());
                if (!subRequestList.isEmpty()){
                    for (Request r : subRequestList){
                        this.requestRepository.delete(r.getId());
                    }
                }
            } else {
                throw new CannotDeleteRequestException("You cannot delete closed request");
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
            Set<ChangeGroup> changeGroups = changeGroupRepository.findByRequestIdWithDetails(requestId,Period.valueOf(period.toUpperCase()));
            fill(changeGroups);
            return changeGroups;
        }catch (IllegalArgumentException e){
            return new HashSet<ChangeGroup>();
        }
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

        requestList.forEach(r -> fill(r));

        return requestList;

    }

    private void fillRequest(Request request) {
        fill(request);

        Request parent = request.getParent();
        if(parent != null) {
            request.setParent(this.getRequestById(parent.getId()).orElse(null));
        }
    }

    private void fillRequest(Request request, Request parent) {
        fill(request);
        request.setParent(parent);
    }

    private void fill(Request request) {
        Person employee = request.getEmployee();
        if(employee != null) {
            employee = personRepository.findOne(employee.getId()).orElseGet(null);

            Role role = roleRepository.findOne(employee.getRole().getId()).orElseGet(null);
            employee.setRole(role);

            request.setEmployee(employee);
        }

        Person manager = request.getManager();
        if(manager != null) {
            manager = personRepository.findOne(manager.getId()).orElseGet(null);
            request.setManager(manager);
        }

        Priority priority = request.getPriority();
        request.setPriority(priorityRepository.findOne(priority.getId()).orElseGet(null));

        Status status = request.getStatus();
        request.setStatus(statusRepository.findOne(status.getId()).orElseGet(null));
    }

    private void fill(Set<ChangeGroup>  changeGroup){
        changeGroup.forEach(cg->cg.getChangeItems().forEach(ci->{
                    ci.setField(fieldRepository.findOne(ci.getField().getId()).get());
                }));
    }
}

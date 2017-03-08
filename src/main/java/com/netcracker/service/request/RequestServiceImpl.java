package com.netcracker.service.request;

import com.netcracker.exception.*;
import com.netcracker.model.entity.*;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.impl.RequestRepositoryImpl;
import com.netcracker.repository.data.interfaces.*;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
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

    private final ChangeItemRepository changeItemRepository;

    private final FieldRepository fieldRepository;

    private final RequestGroupRepository requestGroupRepository;


    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository,
                              PersonRepository personRepository,
                              StatusRepository statusRepository,
                              RoleRepository roleRepository,
                              PriorityRepository priorityRepository,
                              ChangeGroupRepository changeGroupRepository,
                              ChangeItemRepository changeItemRepository,
                              FieldRepository fieldRepository,
                              RequestGroupRepository requestGroupRepository) {
        this.requestRepository = requestRepository;
        this.personRepository = personRepository;
        this.statusRepository = statusRepository;
        this.roleRepository = roleRepository;
        this.priorityRepository = priorityRepository;
        this.changeGroupRepository = changeGroupRepository;
        this.changeItemRepository = changeItemRepository;
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
    public Optional<Request> updateRequest(Request request, Long requestId, String authorName) {
        Optional<Request> oldRequest = requestRepository.findOne(requestId);
        if(!oldRequest.isPresent()) return Optional.empty();
        updateRequestHistory(request,oldRequest.get(),authorName);
        return this.requestRepository.updateRequest(request);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Optional<Request> updateRequestPriority(Long requestId, String priority, String authorName) {
        Optional<Request> futureNewRequest = requestRepository.findOne(requestId);
        if(!futureNewRequest.isPresent()) return Optional.empty();
        Optional<Priority> p = priorityRepository.findPriorityByName(priority);
        if(!p.isPresent()) return Optional.empty();
        Request oldRequest = new Request(futureNewRequest.get());
        futureNewRequest.get().setPriority(new Priority(p.get().getId()));
        updateRequestHistory(futureNewRequest.get(),oldRequest,authorName);
        this.requestRepository.updateRequestPriority(futureNewRequest.get());
        return futureNewRequest;
    }


    private Optional<Request> updateRequestHistory(Request newRequest,  Request oldRequest , String authorName) {
        Optional<Person> author = personRepository.findPersonByEmail(authorName);
        if(!author.isPresent()) return Optional.empty();
        ChangeGroup changeGroup = new ChangeGroup();
        changeGroup.setRequest(new Request(oldRequest.getId()));
        changeGroup.setAuthor(author.get());
        changeGroup.setCreateDate(new Date(System.currentTimeMillis()));
        Set<ChangeItem> changeItemSet = findMismatching(oldRequest, newRequest);
        ChangeGroup newChangeGroup = changeGroupRepository.save(changeGroup).get();
        changeItemSet.forEach(ci->ci.setChangeGroup(new ChangeGroup(newChangeGroup.getId())));
        changeItemSet.forEach(changeItemRepository::save);
        return Optional.of(newRequest);
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

        requestList.forEach(this::fillRequest);

        return requestList;

    }

    @Override
    public List<Request> getAllRequestByEmployee(String employeeEmail, Pageable pageable) {
        Person employee = personRepository.findPersonByEmail(employeeEmail).get();
        List<Request> requestList = requestRepository.queryForList(
                RequestRepositoryImpl.GET_ALL_REQUESTS_BY_EMPLOYEE, pageable, employee.getId());

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

    /**
     * Request should have only id for field that is custom POJO
     * @param oldRequest
     * @param newRequest
     * @return Set<ChangeItem>
     */
    private Set<ChangeItem> findMismatching(Request oldRequest, Request newRequest) {
        Set<ChangeItem> changeItemSet = new HashSet<>();
        Javers javers = JaversBuilder.javers().build();
        Diff diff = javers.compare(oldRequest, newRequest);
        List<ValueChange> change = diff.getChangesByType(ValueChange.class);
        change.forEach(c->{
            ChangeItem changeItem = new ChangeItem();
            Optional affectedObject = c.getAffectedObject();
            if(affectedObject.get() instanceof Request){
                changeItem.setField(fieldRepository.findFieldByName(c.getPropertyName().toUpperCase()).get());
                changeItem.setOldVal(c.getLeft()!=null?c.getLeft().toString():" ");
                changeItem.setNewVal(c.getRight()!=null?c.getRight().toString():" ");
                changeItemSet.add(changeItem);
            }else {
                if(affectedObject.get() instanceof Status){
                    changeItem.setField(fieldRepository.findFieldByName("STATUS").get());
                    changeItem.setOldVal(statusRepository.findOne(Integer.parseInt(c.getLeft().toString())).get().getName());
                    changeItem.setNewVal(statusRepository.findOne(Integer.parseInt(c.getRight().toString())).get().getName());
                    changeItemSet.add(changeItem);
                }
                if(affectedObject.get() instanceof Person){
                    changeItem.setField(fieldRepository.findFieldByName("MANAGER").get());
                    changeItem.setOldVal(personRepository.findOne(Long.parseLong(c.getLeft().toString())).get().getFullName());
                    changeItem.setNewVal(personRepository.findOne(Long.parseLong(c.getRight().toString())).get().getFullName());
                    changeItemSet.add(changeItem);
                }
                if(affectedObject.get() instanceof Priority){
                    changeItem.setField(fieldRepository.findFieldByName("PRIORITY").get());
                    changeItem.setOldVal(priorityRepository.findOne(Integer.parseInt(c.getLeft().toString())).get().getName());
                    changeItem.setNewVal(priorityRepository.findOne(Integer.parseInt(c.getRight().toString())).get().getName());
                    changeItemSet.add(changeItem);
                }
//                if(affectedObject.get() instanceof RequestGroup){
//                    changeItem.setField(fieldRepository.findFieldByName("GROUP").get());
//                    changeItem.setOldVal(requestGroupRepository.findOne(Integer.parseInt(c.getLeft().toString())).get().getName());
//                    changeItem.setNewVal(requestGroupRepository.findOne(Integer.parseInt(c.getRight().toString())).get().getName());
//                }
            }
        });
        List<ReferenceChange> referenceChanges = diff.getChangesByType(ReferenceChange.class);
//        referenceChanges.forEach(referenceChange -> {
//            String fieldName = referenceChange.getPropertyName();
//        });
        return changeItemSet;
    }

}

package com.netcracker.service.sub;

import com.netcracker.exception.BadRequestException;
import com.netcracker.exception.CannotCreateSubRequestException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Priority;
import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Status;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.PriorityRepository;
import com.netcracker.repository.data.interfaces.RequestRepository;
import com.netcracker.repository.data.interfaces.StatusRepository;
import com.netcracker.util.enums.role.RoleEnum;
import com.netcracker.util.enums.status.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class SubRequestServiceImpl implements SubRequestService {

    private static final String INTERNAL_ERROR_MESSAGE = "Internal error.";
    private static final String RESOURCE_NOT_FOUND_MESSAGE = "Resource not found.";
    private static final String ACCESS_DENIED_MESSAGE = "Access denied.";
    private static final String INVALID_ESTIMATE_MESSAGE = "Invalid estimate.";
    private static final String INVALID_STATUS = "Invalid status";
    private static final String PARENT_STATUS_ERROR_MESSAGE = "Parent request is CLOSED or CANCELED.";
    private static final String NOT_PARENT_ERROR_MESSAGE = "This request can not have subtasks.";

    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private PriorityRepository priorityRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private PersonRepository personRepository;

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    public Request createRequest(Long parenId, Request subRequest, Principal principal) throws CannotCreateSubRequestException, ResourceNotFoundException, BadRequestException {

        Request parentRequest = this.loadParent(parenId);
        this.verifyParenStatus(parentRequest);
        subRequest.setParent(this.loadParent(parenId));

        Person creator = this.loadPerson(principal);
        this.verifyPermission(parentRequest, creator);
        subRequest.setEmployee(creator);

        Priority priority = priorityRepository.findOne(subRequest.getPriority().getId())
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
        subRequest.setPriority(priority);

        Status statusFree = statusRepository.findOne(StatusEnum.FREE.getId())
                .orElseThrow(() -> new CannotCreateSubRequestException(INTERNAL_ERROR_MESSAGE));
        subRequest.setStatus(statusFree);

        subRequest.setCreationTime(new Timestamp(new Date().getTime()));
        this.verifyEstimate(subRequest, parentRequest);

        return requestRepository.save(subRequest)
                .orElseThrow(() -> new CannotCreateSubRequestException(INTERNAL_ERROR_MESSAGE));
    }



    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    public Request updateRequest(Long subId, Long parenId, Request subrequest, Principal principal) throws CannotCreateSubRequestException, ResourceNotFoundException, BadRequestException {

        Request parentRequest = this.loadParent(parenId);
        this.verifyParenStatus(parentRequest);

        Request savedRequest = this.loadSubrequest(subId, parentRequest);

        Person updater = this.loadPerson(principal);

        this.verifyPermission(parentRequest, updater);

        if (subrequest.getName()!=null){
            savedRequest.setName(subrequest.getName());
        }

        savedRequest.setDescription(subrequest.getDescription());

        if (subrequest.getStatus()!=null){
            Status newStatus = statusRepository.findOne(subrequest.getStatus().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
            if (newStatus.getId().equals(StatusEnum.CANCELED.getId())){
                throw new BadRequestException(INVALID_STATUS);
            }
            savedRequest.setStatus(newStatus);
        }

        if (subrequest.getPriority()!=null){
            Priority newPriority = priorityRepository.findOne(subrequest.getPriority().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
            savedRequest.setPriority(newPriority);
        }

        if (subrequest.getEstimate()!=null){
            savedRequest.setEstimate(subrequest.getEstimate());
            this.verifyEstimate(subrequest, parentRequest);
        } else {
            savedRequest.setEstimate(null);
        }

        Person creator = personRepository.findOne(savedRequest.getEmployee().getId()).orElseGet(Person::new);
        savedRequest.setEmployee(creator);
        return requestRepository.updateRequest(savedRequest)
                .orElseThrow(() -> new BadRequestException(INTERNAL_ERROR_MESSAGE));
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    public List<Request> getAllSubRequest(Long parentId, Principal principal) throws ResourceNotFoundException, BadRequestException, CannotCreateSubRequestException {

        Request parentRequest = loadParent(parentId);
        Person person = loadPerson(principal);
        this.verifyPermission(parentRequest, person);

        List<Request> requests = requestRepository.getAllSubRequest(parentRequest.getId());
        requests.forEach(request -> {
            Person creator = personRepository.findOne(request.getEmployee().getId()).orElseGet(Person::new);
            request.setEmployee(creator);
        });
        return requests;
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    public void deleteSubRequest(Long parentId, Long subId, Principal principal) throws ResourceNotFoundException, BadRequestException, CannotCreateSubRequestException {
        Request parentRequest = this.loadParent(parentId);
        this.verifyParenStatus(parentRequest);

        Person person = this.loadPerson(principal);
        this.verifyPermission(parentRequest, person);

        Request sub = this.loadSubrequest(subId, parentRequest);
        requestRepository.delete(sub.getId());
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    public List<Status> getStatuses(){
        return statusRepository.findAll();
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    public List<Priority> getPriorities(){
        return priorityRepository.findAll();
    }

    private Person loadPerson(Principal principal) throws ResourceNotFoundException, BadRequestException {
        if (principal != null && principal.getName() != null){
            return personRepository.findPersonByEmail(principal.getName())
                    .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
        } else {
            throw new BadRequestException();
        }
    }

    private Request loadParent(Long id) throws ResourceNotFoundException, BadRequestException {
        if (id != null){
            Request parentRequest = requestRepository.findOne(id)
                    .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
            if (parentRequest.getParent() != null){
                throw new BadRequestException(NOT_PARENT_ERROR_MESSAGE);
            }
            return parentRequest;
        } else {
            throw new BadRequestException();
        }
    }

    private void verifyParenStatus(Request parentRequest) throws BadRequestException {
        if (parentRequest.getStatus().getId().equals(StatusEnum.CLOSED.getId()) ||
                parentRequest.getStatus().getId().equals(StatusEnum.CANCELED.getId())){
            throw new BadRequestException(PARENT_STATUS_ERROR_MESSAGE);
        }
    }

    private void verifyPermission(Request parentRequest, Person person ) throws CannotCreateSubRequestException {
        if (!person.getRole().getId().equals(RoleEnum.ADMINISTRATOR.getId())) {
            if (parentRequest.getManager() ==null || parentRequest.getManager().getId() != person.getId()){
                throw new CannotCreateSubRequestException(ACCESS_DENIED_MESSAGE);
            }
        }
    }

    private Request loadSubrequest(Long id, Request parentRequest) throws ResourceNotFoundException, BadRequestException {
        Request subRequest;
        if (id != null){
            subRequest = requestRepository.findSubrequestByIdAndParent(id, parentRequest.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE));
            return subRequest;
        } else {
            throw new BadRequestException();
        }
    }

    private void verifyEstimate(Request subrequest, Request parentRequest) throws BadRequestException {
        if (subrequest.getEstimate() != null){
            if (new Date(subrequest.getEstimate().getTime()).before(new Date(subrequest.getCreationTime().getTime()))){
                throw new BadRequestException(INVALID_ESTIMATE_MESSAGE);
            }
            if (parentRequest.getEstimate() != null){
                if (new Date(parentRequest.getEstimate().getTime()).before(new Date(subrequest.getEstimate().getTime()))){
                    throw new BadRequestException(INVALID_ESTIMATE_MESSAGE);
                }
            }
        }
    }
}

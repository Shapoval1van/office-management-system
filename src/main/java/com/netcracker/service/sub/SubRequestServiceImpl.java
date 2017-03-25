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
public class SubRequestServiceImpl {

    private final static String PARENT_NOT_FOUND_MESSAGE = "Parent not found.";
    private final static String SUBREQUEST_CHILD_MESSAGE = "Subrequest can not have subrequests.";
    private final static String INVALID_ESTIMATE_MESSAGE = "Invalid estimate";
    private final static String INVALID_PRIORITY_MESSAGE = "Invalid priority";
    private final static String INTERNAL_ERROR_MESSAGE = "Internal error";
    private final static String PERSON_NOT_FOUND_MESSAGE = "Person not found";
    private final static String SUBREQUEST_NOT_FOUND_MESSAGE = "Subrequest not found";
    private final static String ACCESS_DENIED_MESSAGE = "Access denied";
    private final static String CLOSED_REQUEST_ERROR_MESSQGE = "Parent request status is CLOSED";

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
    public Request createRequest(Long parenId, Request subrequest, Principal principal) throws CannotCreateSubRequestException, ResourceNotFoundException, BadRequestException {

        Request parentRequest = this.loadParent(parenId);
        this.verifyParenStatus(parentRequest);
        subrequest.setParent(this.loadParent(parenId));

        Person creator = this.loadPerson(principal);
        this.verifyPermission(parentRequest, creator);
        subrequest.setEmployee(creator);

        Priority priority = this.loadPriority(subrequest.getPriority(), "NORMAL");
        subrequest.setPriority(priority);

        Status statusFree = this.loadStatus(null, StatusEnum.FREE);
        subrequest.setStatus(statusFree);

        subrequest.setCreationTime(new Timestamp(new Date().getTime()));
        this.verifyEstimate(subrequest, parentRequest);

        return requestRepository.save(subrequest)
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
            Status newStatus = this.loadStatus(subrequest.getStatus(), null);
            savedRequest.setStatus(newStatus);
        }

        if (subrequest.getPriority()!=null){
            Priority newPriority = this.loadPriority(subrequest.getPriority(), null);
            savedRequest.setPriority(newPriority);
        }

        if (subrequest.getEstimate()!=null){
            savedRequest.setEstimate(subrequest.getEstimate());
            this.verifyEstimate(subrequest, parentRequest);
        } else {
            savedRequest.setEstimate(null);
        }
        return requestRepository.updateRequest(savedRequest)
                .orElseThrow(() -> new BadRequestException(INTERNAL_ERROR_MESSAGE));
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    public List<Request> getAllSubRequest(Long parentId, Principal principal) throws ResourceNotFoundException, BadRequestException, CannotCreateSubRequestException {

        Request parentRequest = loadParent(parentId);


        Person person = loadPerson(principal);

        this.verifyPermission(parentRequest, person);

        return requestRepository.getAllSubRequest(parentRequest.getId());
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
                    .orElseThrow(() -> new ResourceNotFoundException(PERSON_NOT_FOUND_MESSAGE));
        } else {
            throw new BadRequestException();
        }
    }

    private Request loadParent(Long id) throws ResourceNotFoundException, BadRequestException {
        if (id != null){
            Request parentRequest = requestRepository.findOne(id)
                    .orElseThrow(() -> new ResourceNotFoundException(PARENT_NOT_FOUND_MESSAGE));
            if (parentRequest.getParent() != null){
                throw new BadRequestException(SUBREQUEST_CHILD_MESSAGE);
            }
            return parentRequest;
        } else {
            throw new BadRequestException();
        }
    }

    private void verifyParenStatus(Request parentRequest) throws BadRequestException {
        if (parentRequest.getStatus().getId().equals(StatusEnum.CLOSED.getId()) ||
                parentRequest.getStatus().getId().equals(StatusEnum.CANCELED.getId())){
            throw new BadRequestException(CLOSED_REQUEST_ERROR_MESSQGE);
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
                    .orElseThrow(() -> new ResourceNotFoundException(SUBREQUEST_NOT_FOUND_MESSAGE));
            return subRequest;
        } else {
            throw new BadRequestException();
        }
    }

    private Priority loadPriority(Priority priority, String defaultPriorityName) throws CannotCreateSubRequestException, ResourceNotFoundException {
        if (priority != null){
            return priorityRepository.findOne(priority.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(INVALID_PRIORITY_MESSAGE));
        } else if (defaultPriorityName != null){
            return priorityRepository.findPriorityByName(defaultPriorityName)
                    .orElseThrow(() -> new CannotCreateSubRequestException(INTERNAL_ERROR_MESSAGE));
        }
        return null;
    }

    private Status loadStatus(Status status, StatusEnum defaultStatus) throws ResourceNotFoundException {
        if (status != null){
            return statusRepository.findOne(status.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(INTERNAL_ERROR_MESSAGE));
        } else if (defaultStatus != null){
            return statusRepository.findStatusByName(defaultStatus.getName())
                    .orElseThrow(() -> new ResourceNotFoundException(INTERNAL_ERROR_MESSAGE));
        }
        return null;
    }

    private void verifyEstimate(Request subrequest, Request parentRequest) throws BadRequestException {
        if (subrequest.getEstimate() != null){
            if (subrequest.getEstimate().before(subrequest.getCreationTime())){
                throw new BadRequestException(INVALID_ESTIMATE_MESSAGE);
            }
            if (parentRequest.getEstimate() != null){
                if (parentRequest.getEstimate().before(subrequest.getEstimate()));{
                    throw new BadRequestException(INVALID_ESTIMATE_MESSAGE);
                }
            }
        }
    }
}

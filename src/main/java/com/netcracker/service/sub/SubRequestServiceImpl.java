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

        Request parentRequest = this.verifyParent(parenId);
        subrequest.setParent(this.verifyParent(parenId));

        Person creator = this.verifyPerson(principal, parentRequest);
        subrequest.setEmployee(creator);

        Priority priority = this.verifyPriority(subrequest.getPriority(), "NORMAL");
        subrequest.setPriority(priority);

        Status statusFree = this.verifyStatus(null, StatusEnum.FREE);
        subrequest.setStatus(statusFree);

        subrequest.setCreationTime(new Timestamp(new Date().getTime()));
        this.verifyEstimate(subrequest, parentRequest);

        return requestRepository.save(subrequest)
                .orElseThrow(() -> new CannotCreateSubRequestException(INTERNAL_ERROR_MESSAGE));
    }



    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    public Request updateRequest(Long subId, Long parenId, Request subrequest, Principal principal) throws CannotCreateSubRequestException, ResourceNotFoundException, BadRequestException {

        Request parent = this.verifyParent(parenId);

        Request savedRequest = this.verifySubrequest(subId, parent);

        Person updater = this.verifyPerson(principal, parent);

        if (subrequest.getName()!=null){
            savedRequest.setName(subrequest.getName());
        }

        savedRequest.setDescription(subrequest.getDescription());

        if (subrequest.getStatus()!=null){
            Status newStatus = this.verifyStatus(subrequest.getStatus(), null);
            savedRequest.setStatus(newStatus);
        }

        if (subrequest.getPriority()!=null){
            Priority newPriority = this.verifyPriority(subrequest.getPriority(), null);
            savedRequest.setPriority(newPriority);
        }

        if (subrequest.getEstimate()!=null){
            savedRequest.setEstimate(subrequest.getEstimate());
            this.verifyEstimate(subrequest, parent);
        } else {
            savedRequest.setEstimate(null);
        }
        return requestRepository.updateRequest(savedRequest)
                .orElseThrow(() -> new BadRequestException(INTERNAL_ERROR_MESSAGE));
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    public List<Request> getAllSubRequest(Long parentId, Principal principal) throws ResourceNotFoundException, BadRequestException, CannotCreateSubRequestException {
        Request parent = verifyParent(parentId);
        Person person = verifyPerson(principal, parent);

        return requestRepository.getAllSubRequest(parent.getId());
    }

    @Transactional
    @PreAuthorize("hasAnyAuthority('ROLE_OFFICE MANAGER', 'ROLE_ADMINISTRATOR')")
    public void deleteSubRequest(Long parentId, Long subId, Principal principal) throws ResourceNotFoundException, BadRequestException, CannotCreateSubRequestException {
        Request parent = this.verifyParent(parentId);

        Person person = this.verifyPerson(principal, parent);

        Request sub = this.verifySubrequest(subId, parent);

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

    private Person verifyPerson(Principal principal, Request parentRequest) throws CannotCreateSubRequestException, ResourceNotFoundException {
        Person person;
        if (principal != null && principal.getName() != null){
            person = personRepository.findPersonByEmail(principal.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("Person not found."));
        } else {
            throw new ResourceNotFoundException("Person not found.");
        }

        if (!person.getRole().getId().equals(RoleEnum.ADMINISTRATOR.getId())) {
            if (parentRequest.getManager()==null || parentRequest.getManager().getId() != person.getId()){
                throw new CannotCreateSubRequestException("You can not create subrequest.");
            }
        }
        return person;
    }

    private Request verifyParent(Long id) throws ResourceNotFoundException, BadRequestException {
        Request parentRequest = requestRepository.findOne(id)
                .orElseThrow(() -> new ResourceNotFoundException(PARENT_NOT_FOUND_MESSAGE));
        if (parentRequest.getParent() != null){
            throw new BadRequestException(SUBREQUEST_CHILD_MESSAGE);
        }
        return parentRequest;
    }

    private Request verifySubrequest(Long id, Request parentRequest) throws ResourceNotFoundException, BadRequestException {
        Request subRequest = requestRepository.findOne(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subrequest not found."));

        if (subRequest.getParent()==null){
            throw new BadRequestException("This request is not a subrequest.");
        }

        if (subRequest.getParent().getId() != parentRequest.getId()){
            throw new BadRequestException("This request is not a subrequest.");
        }
        return subRequest;
    }

    private Priority verifyPriority(Priority priority, String defaultPriorityName) throws CannotCreateSubRequestException, ResourceNotFoundException {
        if (priority != null){
            return priorityRepository.findOne(priority.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(INVALID_PRIORITY_MESSAGE));
        } else if (defaultPriorityName != null){
            return priorityRepository.findPriorityByName(defaultPriorityName)
                    .orElseThrow(() -> new CannotCreateSubRequestException(INTERNAL_ERROR_MESSAGE));
        }
        return null;
    }

    private Status verifyStatus(Status status, StatusEnum defaultStatus) throws ResourceNotFoundException {
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

package com.netcracker.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.exception.*;
import com.netcracker.model.dto.FullRequestDTO;
import com.netcracker.model.dto.RequestAssignDTO;
import com.netcracker.model.dto.RequestDTO;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;
import com.netcracker.model.validation.CreateValidatorGroup;
import com.netcracker.model.view.View;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.PriorityRepository;
import com.netcracker.repository.data.interfaces.RequestGroupRepository;
import com.netcracker.repository.data.interfaces.StatusRepository;
import com.netcracker.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/request")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    PriorityRepository priorityRepository;

    @Autowired
    StatusRepository statusRepository;

    @Autowired
    RequestGroupRepository requestGroupRepository;

    private static final String JSON_MEDIA_TYPE = "application/json;";

    @JsonView(View.Public.class)
    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/{requestId}")
    public ResponseEntity<?> getRequest(@PathVariable Long requestId) {
        Optional<Request> request = requestService.getRequestById(requestId);
        if(request.isPresent()) {
            return ResponseEntity.ok(new FullRequestDTO(request.get()));
        } else {
            return new ResponseEntity<>("No such id", HttpStatus.BAD_REQUEST);
        }
    }

    @JsonView(View.Public.class)
    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/sub/{parentId}")
    public ResponseEntity<?> getSubRequest(@PathVariable Long parentId) {
        List<Request> request = null;
        try {
            request = requestService.getAllSubRequest(parentId);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>("No such parentId", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok((request
                .stream()
                .map(FullRequestDTO::new)
                .collect(Collectors.toList())));
    }

    @PostMapping(produces = JSON_MEDIA_TYPE, value = "/addRequest")
    public ResponseEntity<?> addRequest(@Validated(CreateValidatorGroup.class) @RequestBody RequestDTO requestDTO,
                                        Principal principal) {
        try {
            Request request = requestDTO.toRequest();
            Optional<Person> person = personRepository.findPersonByEmail(principal.getName());
            if(person.isPresent()) {
                request.setEmployee(person.get());
            } else {
                return new ResponseEntity<>("No such person", HttpStatus.BAD_REQUEST);
                // TODO log
            }
            requestService.saveRequest(request);
        } catch (CannotCreateRequestException e) {
            return new ResponseEntity<>(e.getDescription(), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("Added");
    }

    @PostMapping(produces = JSON_MEDIA_TYPE, value = "/addSubRequest")
    public ResponseEntity<?> addSubRequest(@Validated(CreateValidatorGroup.class) @RequestBody RequestDTO requestDTO,
                                           Principal principal) {
        try {
            Request subRequest = requestDTO.toRequest();
            Optional<Person> person = personRepository.findPersonByEmail(principal.getName());
            if(person.isPresent()) {
                subRequest.setManager(person.get());
            } else {
                return new ResponseEntity<>("No such person", HttpStatus.BAD_REQUEST);
                // TODO log
            }
            requestService.saveSubRequest(requestDTO.toRequest());
        } catch (CannotCreateSubRequestException e) {
            return new ResponseEntity<>(e.getDescription(), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("Added");
    }

    @PutMapping(produces = JSON_MEDIA_TYPE, value = "/{requestId}/update")
    public ResponseEntity<Request> updateRequest(@Validated(CreateValidatorGroup.class) @PathVariable Long requestId,
                                           @RequestBody RequestDTO requestDTO) {
        Request currentRequest = requestService.getRequestById(requestId).get();
        if (currentRequest==null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        currentRequest.setName(requestDTO.getName());
        currentRequest.setDescription(requestDTO.getDescription());
        currentRequest.setCreationTime(requestDTO.getCreationTime());
        if (requestDTO.getEstimate()!=null)
            currentRequest.setEstimate(requestDTO.getEstimate());
        currentRequest.setEmployee(personRepository.findOne(requestDTO.getEmployee()).get());
        if (requestDTO.getManager()!=null)
            currentRequest.setManager(personRepository.findOne(requestDTO.getManager()).get());
        currentRequest.setPriority(priorityRepository.findOne(requestDTO.getPriority()).get());
        currentRequest.setStatus(statusRepository.findOne(requestDTO.getStatus()).get());
        if (requestDTO.getRequestGroup()!=null)
            currentRequest.setRequestGroup(requestGroupRepository.findOne(requestDTO.getRequestGroup()).get());
        if (requestDTO.getParent()!=null)
            currentRequest.setParent(requestService.getRequestById(requestDTO.getParent()).get());

        requestService.updateRequest(currentRequest);

        return new ResponseEntity<>(currentRequest, HttpStatus.OK);
    }

    @DeleteMapping(produces = JSON_MEDIA_TYPE, value = "/{requestId}/delete")
    public ResponseEntity<Request> deleteRequest(@Validated(CreateValidatorGroup.class) @PathVariable Long requestId) {
        try {
            Request request = requestService.getRequestById(requestId).get();
            if (request==null)
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            requestService.deleteRequestById(requestId);
        } catch (CannotDeleteRequestException | ResourceNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(produces = JSON_MEDIA_TYPE, value = "/assignRequest")
    public ResponseEntity<?> assignRequest(@Validated(CreateValidatorGroup.class) @RequestBody RequestAssignDTO requestAssignDTO,
                                           Principal principal){
        try{
                requestService.assignRequest(requestAssignDTO.getRequestId(), requestAssignDTO.getPersonId(), principal);
        } catch (CannotAssignRequestException e){
            return new ResponseEntity<>(e.getDescription(), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("Assigned");
    }

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/available/{priorityId}")
    public ResponseEntity<?> getRequestList(@PathVariable Integer priorityId, Pageable pageable){
        List<Request> requests = requestService.getAvailableRequestList(priorityId, pageable);

        return ResponseEntity.ok((requests
                .stream()
                .map(FullRequestDTO::new)
                .collect(Collectors.toList())));
    }

}
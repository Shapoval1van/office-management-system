package com.netcracker.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.exception.*;
import com.netcracker.model.dto.*;
import com.netcracker.model.entity.Request;
import com.netcracker.model.validation.CreateValidatorGroup;
import com.netcracker.model.view.View;
import com.netcracker.repository.common.Pageable;
import com.netcracker.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/request")
@Validated
public class RequestController {

    @Autowired
    private RequestService requestService;

    private static final String JSON_MEDIA_TYPE = "application/json;";

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/history/{requestId}")
    public ResponseEntity<?> getRequestHistory(@Pattern(regexp = "(day|all|month)")
                                               @RequestParam(name = "period", defaultValue = "day") String period,
                                               @PathVariable(name = "requestId") Long id) {
        Set<HistoryDTO> historySet = new TreeSet<>((cg1, cg2)->{
            if(cg1.getId()>cg2.getId()) return 1;
            else if(cg1.getId()<cg2.getId()) return -1;
            else return 0;});
        requestService.getRequestHistory(id, period).forEach(changeGroup -> historySet.add(new HistoryDTO(changeGroup)));
        return new ResponseEntity<>(historySet, HttpStatus.OK);
    }


    @PostMapping(value = "/updatePriority/{requestId}")
    public ResponseEntity<?> updateRequestPriority(@Pattern(regexp = "(high|low|normal)")
                                               @RequestParam(name = "priority") String priority,
                                               @PathVariable(name = "requestId") Long id, Principal principal) {
        Optional<Request> newRequest = requestService.updateRequestPriority(id, priority, principal.getName());
        if(!newRequest.isPresent()){
            return new ResponseEntity<>(new MessageDTO("Request not Updated"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new MessageDTO("Request updated"), HttpStatus.OK);
    }

    @JsonView(View.Public.class)
    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/{requestId}")
    public ResponseEntity<?> getRequest(@PathVariable Long requestId) {
        Optional<Request> request = requestService.getRequestById(requestId);
        if(request.isPresent()) {
            return ResponseEntity.ok(new FullRequestDTO(request.get()));
        } else {
            return new ResponseEntity<>(new MessageDTO("No such id"), HttpStatus.BAD_REQUEST);
        }
    }

    @JsonView(View.Public.class)
    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/sub/{parentId}")
    public ResponseEntity<?> getSubRequest(@PathVariable Long parentId) throws ResourceNotFoundException {
        List<Request> request = requestService.getAllSubRequest(parentId);
        return ResponseEntity.ok((request
                .stream()
                .map(FullRequestDTO::new)
                .collect(Collectors.toList())));
    }

    @PostMapping(produces = JSON_MEDIA_TYPE, value = "/addRequest")
    public ResponseEntity<?> addRequest(@Validated(CreateValidatorGroup.class) @RequestBody RequestDTO requestDTO,
                                        Principal principal) throws CannotCreateSubRequestException, CannotCreateRequestException {
        Request request = requestDTO.toRequest();
        requestService.saveRequest(request, principal.getName());
        return ResponseEntity.ok(new MessageDTO("Added"));
    }

    @PostMapping(produces = JSON_MEDIA_TYPE, value = "/addSubRequest")
    public ResponseEntity<?> addSubRequest(@Validated(CreateValidatorGroup.class) @RequestBody RequestDTO requestDTO,
                                           Principal principal) throws CannotCreateSubRequestException {
        Request subRequest = requestDTO.toRequest();
        requestService.saveSubRequest(subRequest, principal.getName());
        return ResponseEntity.ok(new MessageDTO("Added"));
    }

    @PutMapping(produces = JSON_MEDIA_TYPE, value = "/{requestId}/update")
    public ResponseEntity<Request> updateRequest(@Validated(CreateValidatorGroup.class) @PathVariable Long requestId,
                                           @RequestBody RequestDTO requestDTO) {
        Request currentRequest = requestDTO.toRequest();
        currentRequest.setId(requestId);
        requestService.updateRequest(currentRequest, requestId);
        return new ResponseEntity<>(currentRequest, HttpStatus.OK);
    }

    @DeleteMapping(produces = JSON_MEDIA_TYPE, value = "/{requestId}/delete")
    public ResponseEntity<?> deleteRequest(@Validated(CreateValidatorGroup.class) @PathVariable Long requestId) {
        try {
            Optional<Request> request = requestService.getRequestById(requestId);
            if (!request.isPresent())
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            requestService.deleteRequestById(requestId);
        } catch (CannotDeleteRequestException | ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(produces = JSON_MEDIA_TYPE, value = "/assignRequest")
    public ResponseEntity<?> assignRequest(@Validated(CreateValidatorGroup.class) @RequestBody RequestAssignDTO requestAssignDTO,
                                           Principal principal) throws CannotAssignRequestException {
        requestService.assignRequest(requestAssignDTO.getRequestId(), requestAssignDTO.getPersonId(), principal);

        return ResponseEntity.ok(new MessageDTO("Assigned"));
    }

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/available/{priorityId}")
    public ResponseEntity<?> getRequestList(@PathVariable Integer priorityId, Pageable pageable){
        List<Request> requests = requestService.getAvailableRequestList(priorityId, pageable);

        return ResponseEntity.ok((requests
                .stream()
                .map(FullRequestDTO::new)
                .collect(Collectors.toList())));
    }

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/count/{priorityId}")
    public ResponseEntity<?> getCountFree(@PathVariable Integer priorityId){
        Long count = requestService.getCountFree(priorityId);
        return ResponseEntity.ok(count);
    }

}
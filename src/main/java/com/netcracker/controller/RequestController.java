package com.netcracker.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.exception.*;
import com.netcracker.exception.IllegalAccessException;
import com.netcracker.exception.request.RequestNotAssignedException;
import com.netcracker.exception.requestGroup.CannotUpdateStatusException;
import com.netcracker.model.dto.*;
import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Status;
import com.netcracker.model.validation.CreateValidatorGroup;
import com.netcracker.model.view.View;
import com.netcracker.repository.common.Pageable;
import com.netcracker.service.request.RequestService;
import com.netcracker.service.status.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static com.netcracker.controller.RegistrationController.JSON_MEDIA_TYPE;

@RestController
@RequestMapping("/api/request")
@Validated
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private StatusService statusService;

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/history/{requestId}")
    public ResponseEntity<?> getRequestHistory(@Pattern(regexp = "(day|all|month)")
                                               @RequestParam(name = "period", defaultValue = "day") String period,
                                               @PathVariable(name = "requestId") Long id, Pageable pageable) {
        return new ResponseEntity<>(requestService.getRequestHistory(id, period, pageable), HttpStatus.OK);
    }

    @JsonView(View.Public.class)
    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/{requestId}")
    public ResponseEntity<?> getRequest(@PathVariable Long requestId) {
        Optional<Request> request = requestService.getRequestById(requestId);
        return ResponseEntity.ok(new FullRequestDTO(request.get()));
    }

    @PostMapping(produces = JSON_MEDIA_TYPE, value = "/add")
    public ResponseEntity<?> addRequest(@Validated(CreateValidatorGroup.class) @RequestBody RequestDTO requestDTO,
                                        Principal principal) throws CannotCreateSubRequestException,
                                        CannotCreateRequestException, CurrentUserNotPresentException {
        Request request = requestDTO.toRequest();
        requestService.saveRequest(request, principal);
        return ResponseEntity.ok(new MessageDTO("Added"));
    }

    @PutMapping(produces = JSON_MEDIA_TYPE, value = "/{requestId}")
    public ResponseEntity<Request> updateRequest(@PathVariable Long requestId,
                                                 @Validated(CreateValidatorGroup.class)
                                                 @RequestBody RequestDTO requestDTO, Principal principal)
                                                 throws ResourceNotFoundException, IllegalAccessException {
        Request currentRequest = requestDTO.toRequest();
        currentRequest.setId(requestId);
        requestService.updateRequest(currentRequest, requestId, principal);
        return new ResponseEntity<>(currentRequest, HttpStatus.OK);
    }

    @PutMapping(produces = JSON_MEDIA_TYPE, value = "/{requestId}/status/{statusId}")
    public ResponseEntity<?> updateRequestStatus(@PathVariable Long requestId,
                                                 @PathVariable Integer statusId,
                                                 @Validated(CreateValidatorGroup.class)
                                                 @RequestBody RequestDTO requestDTO,
                                                 Principal principal)
                                                 throws ResourceNotFoundException, IllegalAccessException, CannotUpdateStatusException {
        Request currentRequest = requestDTO.toRequest();
        currentRequest.setId(requestId);
        Optional<Status> status = statusService.getStatusById(statusId);
        requestService.changeRequestStatus(currentRequest, status.get(), principal.getName());
        return new ResponseEntity<>(currentRequest, HttpStatus.OK);
    }

    @DeleteMapping(produces = JSON_MEDIA_TYPE, value = "/{requestId}")
    public ResponseEntity<?> deleteRequest(@Validated(CreateValidatorGroup.class)
                                           @PathVariable Long requestId, Principal principal)
            throws CannotDeleteRequestException, ResourceNotFoundException, CannotUpdateStatusException {
        requestService.deleteRequestById(requestId, principal);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(produces = JSON_MEDIA_TYPE, value = "/assign/request/{requestId}")
    public ResponseEntity<?> assignRequest(@Validated(CreateValidatorGroup.class)
                                           @PathVariable Long requestId,
                                           Principal principal) throws CannotAssignRequestException {
        requestService.assignRequest(requestId, principal);
        return ResponseEntity.ok(new MessageDTO("Assigned"));
    }

    @PostMapping(produces = JSON_MEDIA_TYPE, value = "/assign/request")
    public ResponseEntity<?> assignRequest(@Validated(CreateValidatorGroup.class)
                                           @RequestBody RequestAssignDTO requestAssignDTO,
                                           Principal principal)
                                           throws CannotAssignRequestException {
        requestService.assignRequest(requestAssignDTO.getRequestId(), requestAssignDTO.getPersonId(), principal);
        return ResponseEntity.ok(new MessageDTO("Assigned"));
    }

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/available/{priorityId}")
    public ResponseEntity<?> getRequestListByPriority(@PathVariable Integer priorityId, Pageable pageable) {
        Page<Request> requestPage = requestService.getAvailableRequestListByPriority(priorityId, pageable);
        return ResponseEntity.ok(requestPage);
    }

    @PutMapping(produces = JSON_MEDIA_TYPE, value = "/{requestId}/unassig")
    public ResponseEntity<?> unassign(@PathVariable Long requestId, Principal principal)
            throws CannotAssignRequestException, ResourceNotFoundException, CannotUnassignRequestException {
        requestService.unassign(requestId, principal);
        return ResponseEntity.ok(new MessageDTO("Unassigned"));
    }

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/available")
    public ResponseEntity<?> getRequestList(Pageable pageable) {
        Page<Request> requestPage = requestService.getAvailableRequestList(pageable);
        return ResponseEntity.ok(requestPage);
    }


    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/list/my")
    public ResponseEntity<?> getRequestListByEmployee(Pageable pageable, Principal principal) {
        Page<Request> requestPage = requestService.getAllRequestByEmployee(principal, pageable);
        return ResponseEntity.ok(requestPage);
    }

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/list/my/closed")
    public ResponseEntity<?> getClosedRequestListByEmployee(Pageable pageable, Principal principal) {
        Page<Request> requestPage = requestService.getClosedRequestByEmployee(principal, pageable);
        return ResponseEntity.ok(requestPage);
    }


    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/list/assigned")
    public ResponseEntity<?> getAssignedRequestList(Principal principal, Pageable pageable) {
        Page<Request> requestPage = requestService.getAllAssignedRequest(principal, pageable);
        return ResponseEntity.ok(requestPage);
    }

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/list/user/{userId}")
    public ResponseEntity<?> getRequestListByUser(@PathVariable Long userId, Pageable pageable) {
        Page<Request> requestPage = requestService.getAllRequestByUser(userId, pageable);
        return ResponseEntity.ok(requestPage);
    }

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/list/assigned/{managerId}")
    public ResponseEntity<?> getAssignedRequestListByManager(@PathVariable Long managerId, Pageable pageable) {
        Page<Request> requestPage = requestService.getAllAssignedRequestByManager(managerId, pageable);
        return ResponseEntity.ok(requestPage);
    }

    @PutMapping("/{requestId}/grouping")
    @ResponseStatus(HttpStatus.OK)
    public void addRequestToRequestGroup(@RequestBody RequestGroupDTO requestGroupDTO,
                                         @PathVariable("requestId") Long requestId,
                                         Principal principal) throws ResourceNotFoundException,
            IncorrectStatusException, IllegalAccessException, RequestNotAssignedException {
        requestService.addToRequestGroup(requestId, requestGroupDTO.getId(), principal);
    }

    @DeleteMapping("/{requestId}/group")
    @ResponseStatus(HttpStatus.OK)
    public void removeFromRequestGroup(@PathVariable("requestId") Long requestId, Principal principal)
            throws ResourceNotFoundException, IllegalAccessException {
        requestService.removeFromRequestGroup(requestId, principal);
    }

    @GetMapping("/request-group/{requestGroupId}")
    @ResponseStatus(HttpStatus.OK)
    public Page<FullRequestDTO> getRequestByRequestGroup(@PathVariable("requestGroupId") Integer requestGroupId,
                                                         Pageable pageable) {
        return requestService.getFullRequestDTOByRequestGroup(requestGroupId, pageable);
    }
}
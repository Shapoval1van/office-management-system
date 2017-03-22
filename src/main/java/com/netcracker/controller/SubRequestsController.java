package com.netcracker.controller;

import com.netcracker.exception.BadRequestException;
import com.netcracker.exception.CannotCreateSubRequestException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.dto.Page;
import com.netcracker.model.dto.PriorityDTO;
import com.netcracker.model.dto.RequestDTO;
import com.netcracker.model.dto.StatusDTO;
import com.netcracker.model.entity.Request;
import com.netcracker.model.validation.CreateValidatorGroup;
import com.netcracker.service.sub.SubRequestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class SubRequestsController {

    @Autowired
    private SubRequestServiceImpl service;

    @PostMapping("/api/request/{requestId}/subrequests")
    public RequestDTO createSubrequest(@PathVariable Long requestId,
                                       @Validated(CreateValidatorGroup.class) @RequestBody RequestDTO requestDTO,
                                       Principal principal) throws CannotCreateSubRequestException, ResourceNotFoundException {
        Request sub = requestDTO.toRequest();
        return new RequestDTO(service.createRequest(requestId, sub, principal.getName()));
    }

    @GetMapping("/api/request/{requestId}/subrequests")
    public List<RequestDTO> getSubrequests(@PathVariable Long requestId, Principal principal) throws ResourceNotFoundException, BadRequestException {
        return service.getAllSubRequest(requestId, principal.getName()).stream()
                .map(request -> new RequestDTO(request)).collect(Collectors.toList());
    }

    @DeleteMapping("/api/request/{requestId}/subrequests/{subId}")
    public void deleteSubrequest(@PathVariable Long requestId,
                                 @PathVariable Long subId,
                                 Principal principal) throws CannotCreateSubRequestException, ResourceNotFoundException, BadRequestException {
        service.deleteSubRequest(requestId, subId,principal.getName());
    }

    @PutMapping("/api/request/{requestId}/subrequests/{subId}")
    public RequestDTO updateSubrequest(@PathVariable Long requestId,
                                       @PathVariable Long subId,
                                       @Validated(CreateValidatorGroup.class) @RequestBody RequestDTO requestDTO,
                                       Principal principal) throws CannotCreateSubRequestException, ResourceNotFoundException, BadRequestException {
        return new RequestDTO(service.updateRequest(subId, requestId, requestDTO.toRequest(),principal.getName()));
    }

    @GetMapping("/api/priorities")
    public List<PriorityDTO> getPriorities(){
        return service.getPriorities().stream()
                .map(priority -> new PriorityDTO(priority)).collect(Collectors.toList());
    }

    @GetMapping("/api/statuses")
    public List<StatusDTO> getStatuses(){
        return service.getStatuses().stream()
                .map(status -> new StatusDTO(status)).collect(Collectors.toList());
    }


}

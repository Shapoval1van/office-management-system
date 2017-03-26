package com.netcracker.controller;

import com.netcracker.exception.BadRequestException;
import com.netcracker.exception.CannotCreateSubRequestException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.dto.PriorityDTO;
import com.netcracker.model.dto.StatusDTO;
import com.netcracker.model.dto.SubRequestDTO;
import com.netcracker.model.entity.Request;
import com.netcracker.model.validation.CreateValidatorGroup;
import com.netcracker.service.sub.SubRequestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public SubRequestDTO createSubrequest(@PathVariable Long requestId,
                                          @Validated(CreateValidatorGroup.class) @RequestBody SubRequestDTO subRequestDTO,
                                          Principal principal) throws CannotCreateSubRequestException, ResourceNotFoundException, BadRequestException {
        Request sub = subRequestDTO.toRequest();
        return new SubRequestDTO(service.createRequest(requestId, sub, principal));
    }

    @GetMapping("/api/request/{requestId}/subrequests")
    @ResponseStatus(HttpStatus.OK)
    public List<SubRequestDTO> getSubrequests(@PathVariable Long requestId, Principal principal) throws ResourceNotFoundException, BadRequestException, CannotCreateSubRequestException {
        return service.getAllSubRequest(requestId, principal).stream()
                .map(request -> new SubRequestDTO(request)).collect(Collectors.toList());
    }

    @DeleteMapping("/api/request/{requestId}/subrequests/{subId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteSubrequest(@PathVariable Long requestId,
                                 @PathVariable Long subId,
                                 Principal principal) throws CannotCreateSubRequestException, ResourceNotFoundException, BadRequestException {
        service.deleteSubRequest(requestId, subId, principal);
    }

    @PutMapping("/api/request/{requestId}/subrequests/{subId}")
    @ResponseStatus(HttpStatus.OK)
    public SubRequestDTO updateSubrequest(@PathVariable Long requestId,
                                       @PathVariable Long subId,
                                       @Validated(CreateValidatorGroup.class) @RequestBody SubRequestDTO SubRequestDTO,
                                       Principal principal) throws CannotCreateSubRequestException, ResourceNotFoundException, BadRequestException {
        return new SubRequestDTO(service.updateRequest(subId, requestId, SubRequestDTO.toRequest(), principal));
    }

    @GetMapping("/api/priorities")
    @ResponseStatus(HttpStatus.OK)
    public List<PriorityDTO> getPriorities(){
        return service.getPriorities().stream()
                .map(priority -> new PriorityDTO(priority)).collect(Collectors.toList());
    }

    @GetMapping("/api/statuses")
    @ResponseStatus(HttpStatus.OK)
    public List<StatusDTO> getStatuses(){
        return service.getStatuses().stream()
                .map(status -> new StatusDTO(status)).collect(Collectors.toList());
    }


}

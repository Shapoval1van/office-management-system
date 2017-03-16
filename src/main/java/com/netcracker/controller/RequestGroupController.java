package com.netcracker.controller;

import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.exception.IllegalAccessException;
import com.netcracker.exception.IncorrectStatusException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.dto.RequestGroupDTO;
import com.netcracker.model.dto.StatusDTO;
import com.netcracker.model.entity.RequestGroup;
import com.netcracker.model.validation.CreateValidatorGroup;
import com.netcracker.model.validation.UpdateValidatorGroup;
import com.netcracker.repository.common.Pageable;
import com.netcracker.service.requestGroup.RequestGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/request-group")
public class RequestGroupController {

    @Autowired
    private RequestGroupService requestGroupService;

    @GetMapping({"/author/{authorId}"})
    @ResponseStatus(HttpStatus.OK)
    public List<RequestGroup> getRequestGroupByAuthor(@PathVariable("authorId") Long authorId, Pageable pageable) {
        return requestGroupService.getRequestGroupByAuthorId(authorId, pageable);
    }

    @GetMapping({"/author/{authorId}/search/{namePart}"})
    @ResponseStatus(HttpStatus.OK)
    public List<RequestGroup> getRequestGroupByNamePart(@PathVariable("authorId") Long authorId, @PathVariable("namePart") String namePart) {
        return requestGroupService.getRequestGroupByNamePart(namePart, authorId);
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public void createRequestGroup(@Validated(CreateValidatorGroup.class) @RequestBody RequestGroupDTO requestGroupDTO,
                                   Principal principal) throws CurrentUserNotPresentException {
        requestGroupService.saveRequestGroup(requestGroupDTO, principal);
    }

    @PutMapping("/{requestGroupId}")
    @ResponseStatus(HttpStatus.OK)
    public void editRequestGroup(@Validated(UpdateValidatorGroup.class) @RequestBody RequestGroupDTO requestGroupDTO,
                                 @PathVariable("requestGroupId") Integer requestGroupId,
                                 Principal principal) throws ResourceNotFoundException, IllegalAccessException {
        requestGroupDTO.setId(requestGroupId);
        requestGroupService.updateRequestGroup(requestGroupDTO, principal);
    }

    @GetMapping("/count/author/{authorId}")
    @ResponseStatus(HttpStatus.OK)
    public int getRequestGroupCountByAuthor(@PathVariable("authorId") Long authorId) {
        return requestGroupService.getRequestGroupCountByAuthor(authorId);
    }

    @PutMapping("/{requestGroupId}/status")
    @ResponseStatus(HttpStatus.OK)
    public void changeRequestGroupStatus(@PathVariable("requestGroupId") Integer requestGroupId,
                                         @RequestBody StatusDTO statusDTO,
                                         Principal principal) throws ResourceNotFoundException, IncorrectStatusException, IllegalAccessException {
        requestGroupService.setRequestGroupStatus(requestGroupId, statusDTO.getId(), principal);
    }
}

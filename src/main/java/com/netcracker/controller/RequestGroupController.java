package com.netcracker.controller;

import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.model.dto.RequestGroupDTO;
import com.netcracker.model.entity.RequestGroup;
import com.netcracker.repository.common.impl.SimplePageable;
import com.netcracker.service.requestGroup.RequestGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/request-group")
public class RequestGroupController {

    @Autowired
    private RequestGroupService requestGroupService;

    @GetMapping({"/author/{authorId}/page/{pageNumber}/size/{pageSize}"})
    @ResponseStatus(HttpStatus.OK)
    public List<RequestGroup> getRequestGroupByAuthor(@PathVariable("authorId") Long authorId,
                                                      @PathVariable("pageNumber") Integer pageNumber,
                                                      @PathVariable("pageSize") Integer pageSize) {
        return requestGroupService.getRequestGroupByAuthorId(authorId, new SimplePageable(pageSize, pageNumber));
    }

    @GetMapping({"/search/{namePart}"})
    @ResponseStatus(HttpStatus.OK)
    public List<RequestGroup> getRequestGroupByNamePart(@PathVariable("namePart") String namePart) {
        SimplePageable pageable = new SimplePageable(SimplePageable.DEFAULT_PAGE_SIZE, SimplePageable.DEFAULT_PAGE_NUMBER);
        return requestGroupService.getRequestGroupByNamePart(namePart, pageable);
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public void createRequestGroup(RequestGroupDTO requestGroupDTO, Principal principal) throws CurrentUserNotPresentException {
        requestGroupService.saveRequestGroup(requestGroupDTO, principal);
    }
}

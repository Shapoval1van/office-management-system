package com.netcracker.service.sub;


import com.netcracker.exception.BadRequestException;
import com.netcracker.exception.CannotCreateSubRequestException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.Priority;
import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Status;

import java.security.Principal;
import java.util.List;

public interface SubRequestService {

    public Request createRequest(Long parenId, Request subRequest, Principal principal) throws CannotCreateSubRequestException, ResourceNotFoundException, BadRequestException;

    public Request updateRequest(Long subId, Long parenId, Request subrequest, Principal principal) throws CannotCreateSubRequestException, ResourceNotFoundException, BadRequestException;

    public List<Request> getAllSubRequest(Long parentId, Principal principal) throws ResourceNotFoundException, BadRequestException, CannotCreateSubRequestException;

    public List<Status> getStatuses();

    public List<Priority> getPriorities();
}


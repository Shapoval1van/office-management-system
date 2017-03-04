package com.netcracker.service.request;

import com.netcracker.exception.*;
import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Status;
import com.netcracker.repository.common.Pageable;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface RequestService {
    Optional<Request> getRequestById(Long id);
    Optional<Request> saveSubRequest(Request subRequest) throws CannotCreateSubRequestException;
    Optional<Request> saveRequest(Request request) throws CannotCreateRequestException;
    Optional<Request> updateRequest(Request request);
    List<Request> getAllSubRequest(Long parentId) throws ResourceNotFoundException;
    void deleteRequestById(Long id) throws CannotDeleteRequestException, ResourceNotFoundException;
    int changeRequestStatus(Request request, Status status);
    boolean assignRequest(Long requestId, Long personId, Principal principal) throws CannotAssignRequestException;
    List<Request> getAvailableRequestList(Integer priorityId, Pageable pageable);
}
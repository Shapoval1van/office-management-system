package com.netcracker.service.request;

import com.netcracker.model.entity.ChangeGroup;
import com.netcracker.exception.*;
import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Status;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RequestService {
    Optional<Request> getRequestById(Long id);
    Optional<Request> saveSubRequest(Request subRequest, String email) throws CannotCreateSubRequestException;
    Optional<Request> saveRequest(Request request, String email) throws CannotCreateRequestException, CannotCreateSubRequestException;
    Optional<Request> updateRequest(Request request, Long requestId);
    List<Request> getAllSubRequest(Long parentId) throws ResourceNotFoundException;
    void deleteRequestById(Long id) throws CannotDeleteRequestException, ResourceNotFoundException;
    int changeRequestStatus(Request request, Status status);
    boolean assignRequest(Long requestId, Long personId, Principal principal) throws CannotAssignRequestException;
    Set<ChangeGroup> getRequestHistory(Long requestId, String period);
}
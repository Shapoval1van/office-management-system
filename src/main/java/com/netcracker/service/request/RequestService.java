package com.netcracker.service.request;

import com.netcracker.exception.CannotCreateRequestException;
import com.netcracker.exception.CannotCreateSubRequestException;
import com.netcracker.exception.CannotDeleteRequestException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.ChangeGroup;
import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Status;

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
    Set<ChangeGroup> getRequestHistory(Long requestId, String period);
}

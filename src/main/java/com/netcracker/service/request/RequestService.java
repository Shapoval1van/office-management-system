package com.netcracker.service.request;

import com.netcracker.exception.CannotCreateSubRequestException;
import com.netcracker.exception.CannotDeleteRequestException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Status;

import java.util.List;
import java.util.Optional;

public interface RequestService {
    Optional<Request> getRequestById(Long id);
    Optional<Request> saveSubRequest(Request subRequest, Request parentRequest) throws CannotCreateSubRequestException;
    Optional<Request> saveRequest(Request request);
    Optional<Request> updateRequest(Request request);
    List<Request> getAllSubRequest(Request parentRequest);
    void deleteRequestById(Long id) throws CannotDeleteRequestException, ResourceNotFoundException;
    int changeRequestStatus(Request request, Status status);

}

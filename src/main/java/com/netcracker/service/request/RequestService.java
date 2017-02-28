package com.netcracker.service.request;

import com.netcracker.exception.CannotCreateRequestException;
import com.netcracker.exception.CannotCreateSubRequestException;
import com.netcracker.exception.CannotDeleteRequestException;
import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Status;

import java.util.List;
import java.util.Optional;

public interface RequestService {
    Optional<Request> getRequestById(Long id);
    Optional<Request> saveSubRequest(Request subRequest) throws CannotCreateSubRequestException;
    Optional<Request> saveRequest(Request request) throws CannotCreateRequestException;
    Optional<Request> updateRequest(Request request);
    List<Request> getAllSubRequest(Request parentRequest);
    void deleteRequestById(Long id) throws CannotDeleteRequestException;
    int changeRequestStatus(Request request, Status status);

}

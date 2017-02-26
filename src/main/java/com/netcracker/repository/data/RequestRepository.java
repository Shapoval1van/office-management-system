package com.netcracker.repository.data;

import com.netcracker.exception.CannotCreateSubRequestException;
import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Status;
import com.netcracker.repository.common.JdbcRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JdbcRepository<Request, Long> {
    Optional<Request> getRequestById(Long id);
    Optional<Request> saveSubRequest(Request subRequest, Request parentRequest) throws CannotCreateSubRequestException;
    int changeRequestStatus(Request request, Status status);
    List<Request> getAllSubRequest(Request request);
    Optional<Request> updateRequest(Request request);
}

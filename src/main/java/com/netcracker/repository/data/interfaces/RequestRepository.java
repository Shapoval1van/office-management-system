package com.netcracker.repository.data.interfaces;

import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Status;
import com.netcracker.repository.common.JdbcRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JdbcRepository<Request, Long> {
    int changeRequestStatus(Request request, Status status);
    List<Request> getAllSubRequest(Long parentId);
    Optional<Request> updateRequest(Request request);
    int assignRequest(Long requestId, Long personId, Status status);
}

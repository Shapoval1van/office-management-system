package com.netcracker.repository.data.interfaces;

import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Status;
import com.netcracker.repository.common.JdbcRepository;
import com.netcracker.repository.common.Pageable;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JdbcRepository<Request, Long> {
    int changeRequestStatus(Request request, Status status);

    List<Request> getAllSubRequest(Long parentId);

    List<Request> getAllAssignedRequest(Long managerId);

    Optional<Request> updateRequest(Request request);

    int updateRequestPriority(Request request);

    int assignRequest(Long requestId, Long personId, Status status);

    Long countFree(Integer priorityId);

    Long countAllRequestByEmployee(Long employeeId);

    List<Request> findRequestsByRequestGroupId(Integer requestGroupId);

    List<Request> findRequestsByRequestGroupId(Integer requestGroupId, Pageable pageable);

    int updateRequestGroup(Long requestId, Integer requestGroupId);

    int removeRequestFromRequestGroup(Long requestId);
}

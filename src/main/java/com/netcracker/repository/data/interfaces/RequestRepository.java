package com.netcracker.repository.data.interfaces;

import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Priority;
import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Status;
import com.netcracker.repository.common.JdbcRepository;
import com.netcracker.repository.common.Pageable;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JdbcRepository<Request, Long> {
    int changeRequestStatus(Request request, Status status);

    int deleteRequest(Request request);

    List<Request> getAllRequests();

    List<Request> getAllSubRequest(Long parentId);

    List<Request> getAllAssignedRequestByManager(Long managerId, Pageable pageable);

    List<Request> getAllAssignedRequestByManager(Long managerId);

    List<Request> getAllAssignedRequest(Long managerId, Pageable pageable);

    List<Request> getAllRequestByUser(Long userId, Pageable pageable);

    List<Request> getAllRequestByUser(Long userId);

    Optional<Request> updateRequest(Request request);

    int updateRequestPriority(Request request);

    int assignRequest(Long requestId, Long personId, Status status);

    Long countFreeByPriority(Integer priorityId);

    Long countFree();

    Long countAllByUser(Long userId);

    Long countAllAssignedByManager(Long managerId);

    Long countAllAssigned(Long managerId);

    Long countClosedRequestByEmployee(Long personId);

    Long countAllRequestByEmployee(Long employeeId);

    Long countAllRequestByManager(Long managerId);

    Long countRequestsByRequestGroupId(Integer requestGroupId);

    List<Request> findRequestsByRequestGroupId(Integer requestGroupId);

    List<Request> findRequestsByRequestGroupId(Integer requestGroupId, Pageable pageable);

    List<Request> findRequestByEmployeeIdForPeriod(Long personId, String reportPeriod);

    List<Request> findRequestByEmployeeIdForPeriod(Long personId, String reportPeriod, Pageable pageable);

    Long countRequestByEmployeeIdForPeriod(Long personId, String reportPeriod);

    List<Request> findAllAssignedRequestToManagerForPeriod(Long personId, String reportPeriod);

    Long countAllAssignedRequestToManagerForPeriod(Long personId, String reportPeriod);

    List<Request> findAllAssignedRequestToManagerForPeriod(Long personId, String reportPeriod, Pageable pageable);

    int updateRequestGroup(Long requestId, Integer requestGroupId);

    int removeRequestFromRequestGroup(Long requestId);

    List<Request> getRequestsByEmployee(Pageable pageable, Person employee);

    List<Request> getClosedRequestsByEmployee(Pageable pageable, Person person);

    List<Request> getRequestsByEmployeeAndPeriod(Timestamp start, Timestamp end, Person employee);

    List<Request> getFreeRequestsWithPriority(Integer priorityId, Pageable pageable, Priority priority);

    List<Request> getFreeRequests(Pageable pageable);

    List<Request> getFreeRequests();

    Optional<Request> findSubrequestByIdAndParent(Long id, Long parenId);

    int unassign(Long requestId);
}


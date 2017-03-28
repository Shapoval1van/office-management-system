package com.netcracker.service.request;

import com.netcracker.exception.*;
import com.netcracker.exception.IllegalAccessException;
import com.netcracker.exception.request.RequestNotAssignedException;
import com.netcracker.model.dto.FullRequestDTO;
import com.netcracker.model.dto.Page;
import com.netcracker.model.entity.ChangeGroup;
import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Status;
import com.netcracker.repository.common.Pageable;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RequestService {
    Optional<Request> getRequestById(Long id);

    Optional<Request> saveSubRequest(Request subRequest, Principal principal) throws CannotCreateSubRequestException;

    Optional<Request> saveRequest(Request request, Principal principal) throws CannotCreateRequestException, CannotCreateSubRequestException;

    int addToRequestGroup(Long requestId, Integer groupId, Principal principal) throws ResourceNotFoundException, IncorrectStatusException, IllegalAccessException, RequestNotAssignedException;

    int removeFromRequestGroup(Long requestId, Principal principal) throws ResourceNotFoundException, IllegalAccessException;

    Optional<Request> updateRequest(Request request, Long requestId, Principal principal) throws ResourceNotFoundException, IllegalAccessException;

    Optional<Request> updateRequestPriority(Long requestId, String priority, Principal principal);

    List<Request> getAllSubRequest(Long parentId) throws ResourceNotFoundException;

    void deleteRequestById(Long id, Principal principal) throws CannotDeleteRequestException, ResourceNotFoundException;

    int changeRequestStatus(Request request, Status status, String authorName);

    boolean assignRequest(Long requestId, Principal principal) throws CannotAssignRequestException;

    boolean assignRequest(Long requestId, Long personId,  Principal principal) throws CannotAssignRequestException; // Assign to somebody

    Page<Request> getAvailableRequestListByPriority(Integer priorityId, Pageable pageable);

    Page<Request> getAvailableRequestList(Pageable pageable);

    Page<Request> getAllRequestByEmployee(Principal principal, Pageable pageable);

    Page<Request> getClosedRequestByEmployee(Principal principal, Pageable pageable);

    Page<Request> getAllRequestByUser(Long userId, Pageable pageable);

    Page<Request> getAllAssignedRequest(Principal principal, Pageable pageable);

    Page<Request> getAllAssignedRequestByManager(Long managerId, Pageable pageable);

    Set<ChangeGroup> getRequestHistory(Long requestId, String period, Pageable pageable);

    List<Request> getRequestsByRequestGroup(Integer requestGroupId);

    Page<Request> getRequestsByRequestGroup(Integer requestGroupId, Pageable pageable);

    Page<FullRequestDTO> getFullRequestDTOByRequestGroup(Integer requestGroupId, Pageable pageable);

    Optional<Request> updateRequestHistory(Request newRequest, Request oldRequest, String authorName);

    void fill(Request request);

    void checkRequestsForExpiry();

    void unassign(Long requestId, Principal principal) throws CannotUnassignRequestException, ResourceNotFoundException;
}
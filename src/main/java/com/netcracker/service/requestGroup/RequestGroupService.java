package com.netcracker.service.requestGroup;

import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.exception.IncorrectStatusException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.dto.RequestGroupDTO;
import com.netcracker.model.entity.RequestGroup;
import com.netcracker.repository.common.Pageable;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface RequestGroupService {
    List<RequestGroup> getRequestGroupByAuthorId(Long authorId, Pageable pageable);

    List<RequestGroup> getRequestGroupByNamePart(String namePart, Pageable pageable);

    Optional<RequestGroup> saveRequestGroup(RequestGroup requestGroup);

    Optional<RequestGroup> saveRequestGroup(RequestGroupDTO requestGroupDTO, Principal principal) throws CurrentUserNotPresentException;

    Optional<RequestGroup> updateRequestGroup(RequestGroupDTO requestGroupDTO) throws ResourceNotFoundException;

    int getRequestGroupCountByAuthor(Long authorId);

    void setRequestGroupStatus(Integer requestGroupId, Integer statusId) throws ResourceNotFoundException, IncorrectStatusException;
}

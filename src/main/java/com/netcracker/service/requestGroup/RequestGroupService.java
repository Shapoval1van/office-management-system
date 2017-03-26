package com.netcracker.service.requestGroup;

import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.exception.IllegalAccessException;
import com.netcracker.exception.IncorrectStatusException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.exception.requestGroup.CannotCreateRequestGroupException;
import com.netcracker.exception.requestGroup.RequestGroupAlreadyExist;
import com.netcracker.model.dto.Page;
import com.netcracker.model.dto.RequestGroupDTO;
import com.netcracker.model.entity.RequestGroup;
import com.netcracker.repository.common.Pageable;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface RequestGroupService {
    Page<RequestGroup> getRequestGroupByAuthorId(Long authorId, Pageable pageable);

    Page<RequestGroupDTO> getRequestGroupDTOByAuthorId(Long authorId, Pageable pageable);

    List<RequestGroup> getRequestGroupByNamePart(String namePart, Long authorId);

    List<RequestGroupDTO> getRequestGroupDTOByNamePart(String namePart, Long authorId);

    Optional<RequestGroup> saveRequestGroup(RequestGroup requestGroup) throws RequestGroupAlreadyExist;

    RequestGroup saveRequestGroup(RequestGroupDTO requestGroupDTO, Principal principal) throws CannotCreateRequestGroupException, CurrentUserNotPresentException;

    Optional<RequestGroup> updateRequestGroup(RequestGroupDTO requestGroupDTO, Principal principal) throws ResourceNotFoundException, IllegalAccessException, RequestGroupAlreadyExist;

    void removeRequestGroup(Integer requestGroupId, Principal principal) throws ResourceNotFoundException, IllegalAccessException;

    Long getRequestGroupCountByAuthor(Long authorId);

    void setRequestGroupStatus(Integer requestGroupId, Integer statusId, Principal principal) throws ResourceNotFoundException, IncorrectStatusException, IllegalAccessException;

    RequestGroup getRequestGroupById(Integer requestGroupId) throws ResourceNotFoundException;
}

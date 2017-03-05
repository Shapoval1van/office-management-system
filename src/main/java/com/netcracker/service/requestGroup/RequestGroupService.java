package com.netcracker.service.requestGroup;

import com.netcracker.model.entity.RequestGroup;
import com.netcracker.repository.common.Pageable;

import java.util.List;

public interface RequestGroupService  {
    List<RequestGroup> getRequestGroupByAuthorId(Long authorId, Pageable pageable);

    List<RequestGroup> getRequestGroupByNamePart(String namePart, Pageable pageable);
}

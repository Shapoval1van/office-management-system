package com.netcracker.repository.data.interfaces;

import com.netcracker.model.entity.RequestGroup;
import com.netcracker.repository.common.JdbcRepository;
import com.netcracker.repository.common.Pageable;

import java.util.List;

public interface RequestGroupRepository extends JdbcRepository<RequestGroup, Integer> {
    List<RequestGroup> findRequestGroupByAuthorId(Long authorId, Pageable pageable);

    List<RequestGroup> findRequestGroupByNameRegex(String regex, Pageable pageable);
}

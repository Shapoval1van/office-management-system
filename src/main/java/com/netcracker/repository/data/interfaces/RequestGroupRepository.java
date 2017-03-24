package com.netcracker.repository.data.interfaces;

import com.netcracker.model.entity.RequestGroup;
import com.netcracker.repository.common.JdbcRepository;
import com.netcracker.repository.common.Pageable;

import java.util.List;
import java.util.Optional;

public interface RequestGroupRepository extends JdbcRepository<RequestGroup, Integer> {
    List<RequestGroup> findRequestGroupByAuthorId(Long authorId, Pageable pageable);

    List<RequestGroup> findRequestGroupByNameRegex(String regex, Long authorId);

    Long countRequestGroupByAuthor(Long authorId);

    Optional<RequestGroup> findRequestGroupByNameAndAuthor(String name, Long authorId);
}

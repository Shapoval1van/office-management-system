package com.netcracker.repository.data.interfaces;

import com.netcracker.model.entity.Priority;
import com.netcracker.repository.common.JdbcRepository;

import java.util.Optional;

public interface PriorityRepository extends JdbcRepository<Priority, Integer> {

    Optional<Priority> findPriorityByName(String name);
}

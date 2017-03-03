package com.netcracker.repository.data.interfaces;

import com.netcracker.model.entity.Role;
import com.netcracker.model.entity.Status;
import com.netcracker.repository.common.JdbcRepository;

import java.util.Optional;

public interface StatusRepository  extends JdbcRepository<Status, Integer> {
    Optional<Status> findStatusByName(String name);
}

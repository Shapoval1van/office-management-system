package com.netcracker.repository.data;


import com.netcracker.model.entity.Role;
import com.netcracker.repository.common.JdbcRepository;

import java.util.Optional;

public interface RoleRepository extends JdbcRepository<Role, Integer> {
    Optional<Role> findRoleByName(String name);
}

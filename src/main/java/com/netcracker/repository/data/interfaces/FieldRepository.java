package com.netcracker.repository.data.interfaces;


import com.netcracker.model.entity.Field;
import com.netcracker.repository.common.JdbcRepository;

import java.util.Optional;

public interface FieldRepository extends JdbcRepository<Field, Integer> {
    Optional<Field> findFieldByName(String name);
}

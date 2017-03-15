package com.netcracker.repository.data.impl;

import com.netcracker.model.entity.Priority;
import com.netcracker.repository.common.GenericJdbcRepository;
import com.netcracker.repository.data.interfaces.PriorityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class PriorityRepositoryImpl extends GenericJdbcRepository<Priority, Integer> implements PriorityRepository {

    public static final String PRIORITY_ID_COLUMN = "priority_id";
    public static final String NAME_COLUMN = "name";

    @Value("${priority.find.by.name}")
    private String FIND_BY_NAME;

    public PriorityRepositoryImpl() {
        super(Priority.TABLE_NAME, Priority.ID_COLUMN);
    }

    @Override
    public Map<String, Object> mapColumns(Priority priority) {
        Map<String, Object> columns = new HashMap<>();
        columns.put(PRIORITY_ID_COLUMN, priority.getId());
        columns.put(NAME_COLUMN, priority.getName());
        return columns;
    }

    @Override
    public RowMapper<Priority> mapRow() {
        return (resultSet, i) -> {
            Priority priority = new Priority();
            priority.setId(resultSet.getInt(PRIORITY_ID_COLUMN));
            priority.setName(resultSet.getString(NAME_COLUMN));
            return priority;
        };
    }

    @Override
    public Optional<Priority> findPriorityByName(String name) {
        return super.queryForObject(FIND_BY_NAME, name.toUpperCase());
    }
}

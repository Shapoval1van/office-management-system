package com.netcracker.repository.data;

import com.netcracker.model.entity.Priority;
import com.netcracker.repository.common.GenericJdbcRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class PriorityRepositoryImpl extends GenericJdbcRepository<Priority, Integer> implements PriorityRepository {

    public static final String PRIORITY_ID_COLUMN = "priority_id";
    public static final String NAME_COLUMN = "name";

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
}

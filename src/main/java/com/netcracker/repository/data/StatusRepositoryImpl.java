package com.netcracker.repository.data;

import com.netcracker.model.entity.Status;
import com.netcracker.repository.common.GenericJdbcRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class StatusRepositoryImpl extends GenericJdbcRepository<Status, Integer> implements StatusRepository {

    public static final String STATUS_ID_COLUMN = "status_id";
    public static final String NAME_COLUMN = "name";

    public StatusRepositoryImpl() {
        super(Status.TABLE_NAME, Status.ID_COLUMN);
    }

    @Override
    public Map<String, Object> mapColumns(Status status) {
        Map<String, Object> columns = new HashMap<>();
        columns.put(STATUS_ID_COLUMN, status.getId());
        columns.put(NAME_COLUMN, status.getName());
        return columns;
    }

    @Override
    public RowMapper<Status> mapRow() {
        return (resultSet, i) -> {
            Status status = new Status();
            status.setId(resultSet.getInt(STATUS_ID_COLUMN));
            status.setName(resultSet.getString(NAME_COLUMN));
            return status;
        };
    }
}

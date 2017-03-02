package com.netcracker.repository.data;

import com.netcracker.model.entity.RequestGroup;
import com.netcracker.repository.common.GenericJdbcRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class RequestGroupRepositoryImpl extends GenericJdbcRepository<RequestGroup, Integer> implements RequestGroupRepository {

    public static final String PRIORITY_ID_COLUMN = "request_group_id";
    public static final String NAME_COLUMN = "name";

    public RequestGroupRepositoryImpl() {
        super(RequestGroup.TABLE_NAME, RequestGroup.ID_COLUMN);
    }

    @Override
    public Map<String, Object> mapColumns(RequestGroup requestGroup) {
        Map<String, Object> columns = new HashMap<>();
        columns.put(PRIORITY_ID_COLUMN, requestGroup.getId());
        columns.put(NAME_COLUMN, requestGroup.getName());
        return columns;
    }

    @Override
    public RowMapper<RequestGroup> mapRow() {
        return (resultSet, i) -> {
            RequestGroup requestGroup = new RequestGroup();
            requestGroup.setId(resultSet.getInt(PRIORITY_ID_COLUMN));
            requestGroup.setName(resultSet.getString(NAME_COLUMN));
            return requestGroup;
        };
    }
}

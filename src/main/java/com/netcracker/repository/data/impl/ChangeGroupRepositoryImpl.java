package com.netcracker.repository.data.impl;

import com.netcracker.model.entity.ChangeGroup;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;
import com.netcracker.repository.common.GenericJdbcRepository;
import com.netcracker.repository.data.ChangeGroupRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ChangeGroupRepositoryImpl extends GenericJdbcRepository<ChangeGroup, Long> implements ChangeGroupRepository {

    public static final String CHANG_GROUP_ID_COLUMN = "change_group_id";
    public static final String CREATED_COLUMN = "created";
    public static final String AUTHOR_COLUMN = "author_id";
    public static final String REQUEST_COLUMN = "request_id";

    public ChangeGroupRepositoryImpl() {
        super(ChangeGroup.TABLE_NAME, ChangeGroup.ID_COLUMN);
    }

    @Override
    public Map<String, Object> mapColumns(ChangeGroup entity) {
        Map<String, Object> columns = new HashMap<>();
        columns.put(CHANG_GROUP_ID_COLUMN, entity.getId());
        columns.put(CREATED_COLUMN, entity.getCreateDate());
        columns.put(AUTHOR_COLUMN, entity.getAuthor().getId());
        columns.put(REQUEST_COLUMN, entity.getRequest().getId());
        return columns;
    }

    @Override
    public RowMapper<ChangeGroup> mapRow() {
        return new RowMapper<ChangeGroup>() {
            @Override
            public ChangeGroup mapRow(ResultSet resultSet, int i) throws SQLException {
                ChangeGroup changeGroup = new ChangeGroup();
                changeGroup.setId(resultSet.getLong(CHANG_GROUP_ID_COLUMN));
                changeGroup.setCreateDate(resultSet.getDate(CREATED_COLUMN));
                Object authorId = resultSet.getObject(AUTHOR_COLUMN);
                if (authorId != null) {
                    changeGroup.setAuthor(new Person((Long) authorId));
                }
                Object requestId = resultSet.getObject(REQUEST_COLUMN);
                if (requestId != null) {
                    changeGroup.setRequest(new Request((Long) requestId));
                }
                return changeGroup;
            }
        };
    }
}

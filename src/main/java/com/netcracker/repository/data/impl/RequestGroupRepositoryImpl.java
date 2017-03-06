package com.netcracker.repository.data.impl;

import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.RequestGroup;
import com.netcracker.repository.common.GenericJdbcRepository;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.interfaces.RequestGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RequestGroupRepositoryImpl extends GenericJdbcRepository<RequestGroup, Integer> implements RequestGroupRepository {

    public static final String REQUEST_GROUP_ID_COLUMN = "request_group_id";
    public static final String NAME_COLUMN = "name";
    public static final String AUTHOR_COLUMN = "author_id";

    private static final String GET_REQUEST_GROUP_BY_AUTHOR_ID = "SELECT request_group_id, name, author_id FROM request_group WHERE author_id = ?";
    private static final String GET_REQUEST_GROUP_BY_NAME_PART = "SELECT request_group_id, name, author_id FROM request_group WHERE name ~* ?";
    private static final String COUNT_REQUEST_GROUP_BY_AUTHOR = "SELECT COUNT(*) FROM request_group WHERE author_id = ?";


    private static final Logger LOGGER = LoggerFactory.getLogger(RequestGroupRepositoryImpl.class);

    public RequestGroupRepositoryImpl() {
        super(RequestGroup.TABLE_NAME, RequestGroup.ID_COLUMN);
    }

    @Override
    public Map<String, Object> mapColumns(RequestGroup requestGroup) {
        Map<String, Object> columns = new HashMap<>();
        columns.put(REQUEST_GROUP_ID_COLUMN, requestGroup.getId());
        columns.put(NAME_COLUMN, requestGroup.getName());
        columns.put(AUTHOR_COLUMN, requestGroup.getAuthor().getId());
        return columns;
    }

    @Override
    public RowMapper<RequestGroup> mapRow() {
        return (resultSet, i) -> {
            RequestGroup requestGroup = new RequestGroup();
            requestGroup.setId(resultSet.getInt(REQUEST_GROUP_ID_COLUMN));
            requestGroup.setName(resultSet.getString(NAME_COLUMN));
            requestGroup.setAuthor(new Person(resultSet.getLong(AUTHOR_COLUMN)));
            return requestGroup;
        };
    }

    @Override
    public List<RequestGroup> findRequestGroupByAuthorId(Long authorId, Pageable pageable) {
        LOGGER.debug("Get {} page of request group ");
        return super.queryForList(GET_REQUEST_GROUP_BY_AUTHOR_ID, pageable, authorId);
    }

    @Override
    public List<RequestGroup> findRequestGroupByNameRegex(String regex, Pageable pageable) {
        LOGGER.debug("Search request group name that matches with {} ", regex);
        return super.queryForList(GET_REQUEST_GROUP_BY_NAME_PART, pageable, regex);
    }

    @Override
    public int countRequestGroupByAuthor(Long authorId) {
        return super.jdbcTemplate.queryForObject(COUNT_REQUEST_GROUP_BY_AUTHOR, Integer.class, authorId);
    }
}

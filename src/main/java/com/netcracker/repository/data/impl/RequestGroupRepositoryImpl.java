package com.netcracker.repository.data.impl;

import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.RequestGroup;
import com.netcracker.repository.common.GenericJdbcRepository;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.interfaces.RequestGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class RequestGroupRepositoryImpl extends GenericJdbcRepository<RequestGroup, Integer> implements RequestGroupRepository {

    public static final String REQUEST_GROUP_ID_COLUMN = "request_group_id";
    public static final String NAME_COLUMN = "name";
    public static final String AUTHOR_COLUMN = "author_id";

    @Value("${request.group.find.by.author}")
    private String GET_REQUEST_GROUP_BY_AUTHOR_ID;

    @Value("${request.group.find.by.name.part}")
    private String GET_REQUEST_GROUP_BY_NAME_PART;

    @Value("${request.group.count.by.author}")
    private String COUNT_REQUEST_GROUP_BY_AUTHOR;

    @Value("${request.group.find.by.name.author}")
    private String FIND_REQUEST_GROUP_BY_NAME_AND_AUTHOR;

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
    public List<RequestGroup> findRequestGroupByNameRegex(String regex, Long authorId) {
        LOGGER.debug("Search request group name that matches with {}. Author: {}", regex, authorId);
        return super.queryForList(GET_REQUEST_GROUP_BY_NAME_PART, regex, authorId);
    }

    @Override
    public Long countRequestGroupByAuthor(Long authorId) {
        return super.jdbcTemplate.queryForObject(COUNT_REQUEST_GROUP_BY_AUTHOR, Long.class, authorId);
    }

    @Override
    public Optional<RequestGroup> findRequestGroupByNameAndAuthor(String name, Long authorId) {
        return super.queryForObject(FIND_REQUEST_GROUP_BY_NAME_AND_AUTHOR, name, authorId);
    }
}

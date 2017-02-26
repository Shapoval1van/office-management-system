package com.netcracker.repository.data;

import com.netcracker.exception.CannotCreateSubRequestException;
import com.netcracker.model.entity.*;
import com.netcracker.repository.common.GenericJdbcRepository;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class RequestRepositoryImpl extends GenericJdbcRepository<Request, Long> implements RequestRepository {

    public static final String REQUEST_ID_COLUMN = "request_id";
    public static final String NAME_COLUMN = "name";
    public static final String DESCRIPTION_COLUMN = "description";
    public static final String CREATION_TIME_COLUMN = "creation_time";
    public static final String ESTIMATE_COLUMN = "estimate";
    public static final String STATUS_ID_COLUMN = "status_id";
    public static final String EMPLOYEE_ID_COLUMN = "employee_id";
    public static final String MANAGER_ID_COLUMN = "manager_id";
    public static final String PARENT_ID_COLUMN = "parent_id";
    public static final String PRIORITY_ID_COLUMN = "priority_id";
    public static final String REQUEST_GROUP_ID_COLUMN = "request_group_id";

    private final String UPDATE_REQUEST_STATUS = "UPDATE " + TABLE_NAME + " SET status_id = ? WHERE request_id = ?";

    private final String FIND_ALL_SUB_REQUEST = "SELECT  request_id, name, description, creation_time, " +
            "estimate, status_id, employee_id, manager_id, priority_id, request_group_id, parent_id" + " FROM " +
            TABLE_NAME + " WHERE parent_id = ?";

    public RequestRepositoryImpl() {
        super(Request.TABLE_NAME, Request.ID_COLUMN);
    }

    @Override
    public Map<String, Object> mapColumns(Request entity) {
        Map<String, Object> columns = new HashMap<>();
        columns.put(REQUEST_ID_COLUMN, entity.getId());
        columns.put(NAME_COLUMN, entity.getName());
        columns.put(DESCRIPTION_COLUMN, entity.getDescription());
        columns.put(CREATION_TIME_COLUMN, entity.getCreationTime());
        columns.put(ESTIMATE_COLUMN, entity.getEstimate());
        columns.put(STATUS_ID_COLUMN, entity.getStatus().getId());
        columns.put(EMPLOYEE_ID_COLUMN, entity.getEmployee().getId());
        columns.put(PRIORITY_ID_COLUMN, entity.getPriority().getId());

        // table can contain null manager, parent and request group object
        Person manager = entity.getManager();
        if (manager != null) {
            columns.put(MANAGER_ID_COLUMN, entity.getManager().getId());
        }

        Request parent = entity.getParent();
        if (parent != null) {
            columns.put(PARENT_ID_COLUMN, entity.getParent().getId());
        }

        RequestGroup requestGroup = entity.getRequestGroup();
        if (requestGroup != null) {
            columns.put(REQUEST_GROUP_ID_COLUMN, entity.getRequestGroup().getId());
        }
        return columns;
    }

    @Override
    public RowMapper<Request> mapRow() {
        return (resultSet, i) -> {
            Request request = new Request();
            request.setId(resultSet.getLong(REQUEST_ID_COLUMN));
            request.setName(resultSet.getString(NAME_COLUMN));
            request.setDescription(resultSet.getString(DESCRIPTION_COLUMN));
            request.setCreationTime(resultSet.getTimestamp(CREATION_TIME_COLUMN));
            request.setEstimate(resultSet.getTimestamp(ESTIMATE_COLUMN));
            request.setStatus(new Status(resultSet.getInt(STATUS_ID_COLUMN)));
            request.setEmployee(new Person(resultSet.getLong(EMPLOYEE_ID_COLUMN)));
            request.setPriority(new Priority(resultSet.getInt(PRIORITY_ID_COLUMN)));

            // table can contain null manager, parent and request group object
            Object managerId = resultSet.getObject(MANAGER_ID_COLUMN);
            if (managerId != null) {
                request.setManager(new Person((Long) managerId));
            }
            Object parentId = resultSet.getObject(PARENT_ID_COLUMN);
            if (parentId != null) {
                request.setParent(new Request((Long) parentId));
            }
            Object requestGroupId = resultSet.getObject(REQUEST_GROUP_ID_COLUMN);
            if (requestGroupId != null) {
                request.setRequestGroup(new RequestGroup((Integer) requestGroupId));
            }
            return request;
        };
    }

    @Override
    public Optional<Request> getRequestById(Long id) {
        return super.findOne(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Optional<Request> saveSubRequest(Request subRequest, Request parentRequest) throws CannotCreateSubRequestException{
        if (parentRequest.getId()!=null && subRequest!=null){
            if (parentRequest.getParent()==null){
                subRequest.setParent(parentRequest);
                subRequest.setEmployee(parentRequest.getEmployee());
                subRequest.setManager(parentRequest.getManager());
                subRequest.setEstimate(parentRequest.getEstimate());
                subRequest.setStatus(new Status(2));
                subRequest.setPriority(parentRequest.getPriority());
                subRequest.setRequestGroup(parentRequest.getRequestGroup());
                return super.save(subRequest);
            }
            else throw new CannotCreateSubRequestException("You cannot create request to sub request!");
        }
        else return Optional.empty();
    }

    @Transactional
    @Override
    public int changeRequestStatus(Request request, Status status) {
        return getJdbcTemplate().update(UPDATE_REQUEST_STATUS, status.getId().intValue(), request.getId().intValue());
    }


    @Override
    public List<Request> getAllSubRequest(Request parentRequest) {
        return super.queryForList(FIND_ALL_SUB_REQUEST, parentRequest.getId().intValue());
    }

    @Transactional
    @Override
    public Optional<Request> updateRequest(Request request) {
        if (request.getId() != null) {
            return super.save(request);
        } else return Optional.empty();
    }

}

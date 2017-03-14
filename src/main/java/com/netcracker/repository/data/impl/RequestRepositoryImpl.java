package com.netcracker.repository.data.impl;

import com.netcracker.model.entity.*;
import com.netcracker.repository.common.GenericJdbcRepository;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.interfaces.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;

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

    @Value("${request.find.all.available.by.priority}")
    public String GET_AVAILABLE_REQUESTS_BY_PRIORITY;

    @Value("${request.find.all.available}")
    public String GET_AVAILABLE_REQUESTS;

    @Value("${request.find.all.by.employee}")
    public String GET_ALL_REQUESTS_BY_EMPLOYEE;

    @Value("${request.update.status}")
    private String UPDATE_REQUEST_STATUS;

    @Value("${request.update.priority}")
    private String UPDATE_REQUEST_PRIORITY;

    @Value("${request.find.all.sub.request}")
    private String FIND_ALL_SUB_REQUEST;

    @Value("${request.assign}")
    private String ASSIGN_REQUEST_TO_PERSON;

    @Value("${request.count.by.priority}")
    private String COUNT_WITH_PRIORITY;

    @Value("${request.find.by.request.group}")
    private String GET_REQUESTS_BY_REQUEST_GROUP_ID;

    @Value("${request.count.by.employee}")
    private String COUNT_ALL_REQUEST_BY_EMPLOYEE;

    @Value("${request.update.group}")
    private String UPDATE_REQUEST_GROUP;

    @Autowired
    private MessageSource messageSource;

    public RequestRepositoryImpl() {
        super(Request.TABLE_NAME, Request.ID_COLUMN);
    }

    public List<Request> getRequests(Integer priorityId, Pageable pageable, Optional<Priority> priority) {
        return priority.isPresent() ? this.queryForList(
                GET_AVAILABLE_REQUESTS_BY_PRIORITY, pageable, priorityId)
                : this.queryForList(GET_AVAILABLE_REQUESTS, pageable);
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
        } else {
            columns.put(MANAGER_ID_COLUMN, null);
        }

        Request parent = entity.getParent();
        if (parent != null) {
            columns.put(PARENT_ID_COLUMN, entity.getParent().getId());
        } else {
            columns.put(PARENT_ID_COLUMN, null);
        }


        RequestGroup requestGroup = entity.getRequestGroup();
        if (requestGroup != null) {
            columns.put(REQUEST_GROUP_ID_COLUMN, entity.getRequestGroup().getId());
        } else {
            columns.put(REQUEST_GROUP_ID_COLUMN, null);
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
            Long requestGroupId = (Long) resultSet.getObject(REQUEST_GROUP_ID_COLUMN);
            if (requestGroupId != null) {
                request.setRequestGroup(new RequestGroup(requestGroupId.intValue()));
            }
            return request;
        };
    }


    @Override
    public int changeRequestStatus(Request request, Status status) {
        return getJdbcTemplate().update(UPDATE_REQUEST_STATUS, status.getId().intValue(), request.getId().intValue());
    }

    public List<Request> getRequestsByEmployee(Pageable pageable, Person employee) {
        return this.queryForList(GET_ALL_REQUESTS_BY_EMPLOYEE, pageable, employee.getId());
    }

    @Override
    public List<Request> getAllSubRequest(Long parentId) {
        return super.queryForList(FIND_ALL_SUB_REQUEST, parentId);
    }


    @Override
    public Optional<Request> updateRequest(Request request) {
        if (request.getId() != null) {
            return super.save(request);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public int updateRequestPriority(Request request) {
        return getJdbcTemplate().update(UPDATE_REQUEST_PRIORITY, request.getPriority().getId(), request.getId());
    }

    @Override
    public int assignRequest(Long requestId, Long personId, Status status) {
        return getJdbcTemplate().update(ASSIGN_REQUEST_TO_PERSON, personId, status.getId(), requestId);
    }

    @Override
    public Long countFree(Integer priorityId) {
        return getJdbcTemplate().queryForObject(COUNT_WITH_PRIORITY, Long.class, priorityId);
    }

    @Override
    public Long countAllRequestByEmployee(Long employeeID) {
        return getJdbcTemplate().queryForObject(COUNT_ALL_REQUEST_BY_EMPLOYEE, Long.class, employeeID);
    }

    @Override
    public List<Request> findRequestsByRequestGroupId(Integer requestGroupId) {
        return super.queryForList(GET_REQUESTS_BY_REQUEST_GROUP_ID, requestGroupId);
    }

    @Override
    public List<Request> findRequestsByRequestGroupId(Integer requestGroupId, Pageable pageable) {
        return super.queryForList(GET_REQUESTS_BY_REQUEST_GROUP_ID, pageable, requestGroupId);
    }

    @Override
    public List<Request> findRequestByEmployeeIdForPeriod(Long personId, String reportPeriod) {
        Locale locale = LocaleContextHolder.getLocale();
        if(reportPeriod == null){
            return new ArrayList<>();
        }
        reportPeriod = reportPeriod.toUpperCase();
        switch (reportPeriod){
            case "month":
                return super.queryForList(messageSource.getMessage("request.all.per.date", new Object[]{"month"}, locale), personId);
            case "quarter":
                return super.queryForList(messageSource.getMessage("request.all.per.date", new Object[]{"quarter"}, locale), personId);
            case "year":
                return super.queryForList(messageSource.getMessage("request.all.per.date", new Object[]{"year"}, locale), personId);
            default:
                return new ArrayList<>();
        }
    }

    @Override
    public int updateRequestGroup(Long requestId, Integer requestGroupId) {
        return getJdbcTemplate().update(UPDATE_REQUEST_GROUP, requestGroupId, requestId);
    }

    @Override
    public List<Request> findRequestByEmployeeIdForPeriod(Long personId, String reportPeriod, Pageable pageable) {
        Locale locale = LocaleContextHolder.getLocale();
        if (reportPeriod == null) {
            return new ArrayList<>();
        }
        reportPeriod = reportPeriod.toUpperCase();
        switch (reportPeriod) {
            case "month":
                return super.queryForList(messageSource.getMessage("request.all.per.date", new Object[]{"month"}, locale), pageable, personId);
            case "quarter":
                return super.queryForList(messageSource.getMessage("request.all.per.date", new Object[]{"quarter"}, locale), pageable, personId);
            case "year":
                return super.queryForList(messageSource.getMessage("request.all.per.date", new Object[]{"year"}, locale), pageable, personId);
            default:
                return new ArrayList<>();
        }
    }

    @Override
    public List<Request> findRequestByManagerIdForPeriod(Long personId, String reportPeriod) {
        Locale locale = LocaleContextHolder.getLocale();
        if (reportPeriod == null) {
            return new ArrayList<>();
        }
        reportPeriod = reportPeriod.toUpperCase();
        switch (reportPeriod) {
            case "month":
                return super.queryForList(messageSource.getMessage("request.all.per.date.by.manager", new Object[]{"month"}, locale), personId);
            case "quarter":
                return super.queryForList(messageSource.getMessage("request.all.per.date.by.manager", new Object[]{"quarter"}, locale), personId);
            case "year":
                return super.queryForList(messageSource.getMessage("request.all.per.date.by.manager", new Object[]{"year"}, locale), personId);
            default:
                return new ArrayList<>();
        }
    }

    @Override
    public List<Request> findRequestByManagerIdForPeriod(Long personId, String reportPeriod, Pageable pageable) {
        Locale locale = LocaleContextHolder.getLocale();
        if (reportPeriod == null) {
            return new ArrayList<>();
        }
        reportPeriod = reportPeriod.toUpperCase();
        switch (reportPeriod) {
            case "month":
                return super.queryForList(messageSource.getMessage("request.all.per.date.by.manager", new Object[]{"month"}, locale), pageable, personId);
            case "quarter":
                return super.queryForList(messageSource.getMessage("request.all.per.date.by.manager", new Object[]{"quarter"}, locale), pageable, personId);
            case "year":
                return super.queryForList(messageSource.getMessage("request.all.per.date.by.manager", new Object[]{"year"}, locale), pageable, personId);
            default:
                return new ArrayList<>();
        }

    }

    @Override
    public int removeRequestFromRequestGroup(Long requestId) {
        return getJdbcTemplate().update(UPDATE_REQUEST_GROUP, null, requestId);
    }

    @Override
    public Optional<Request> findOne(Long requestId) {
        return super.findOne(requestId);
    }
}

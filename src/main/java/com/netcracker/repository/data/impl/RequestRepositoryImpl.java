package com.netcracker.repository.data.impl;

import com.netcracker.model.entity.*;
import com.netcracker.repository.common.GenericJdbcRepository;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.interfaces.RequestRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
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

    @Value("${request.find.all.by.user}")
    public String FIND_ALL_BY_USER;

    @Value("${request.find.all}")
    public String FIND_ALL_REQUEST;

    @Value("${request.find.all.assigned.by.manager}")
    public String FIND_ALL_ASSIGNED_BY_MANAGER;

    @Value("${request.find.all.assigned}")
    public String FIND_ALL_ASSIGNED;

    @Value("${request.find.all.available}")
    public String GET_AVAILABLE_REQUESTS;

    @Value("${request.find.all.by.employee}")
    public String GET_ALL_REQUESTS_BY_EMPLOYEE;

    @Value("${request.find.all.by.manager}")
    public String GET_ALL_REQUESTS_BY_MANAGER;

    @Value("${request.delete}")
    private String DELETE_REQUEST;

    @Value("${request.update.status}")
    private String UPDATE_REQUEST_STATUS;

    @Value("${request.update.priority}")
    private String UPDATE_REQUEST_PRIORITY;

    @Value("${request.find.all.sub.request}")
    private String FIND_ALL_SUB_REQUEST;

    @Value("${request.assign}")
    private String ASSIGN_REQUEST_TO_PERSON;

    @Value("${request.unassign}")
    private String UNASSIGN_REQUEST;

    @Value("${request.count.all.by.user}")
    private String COUNT_ALL_BY_USER;

    @Value("${request.count.all.assigned.by.manager}")
    private String COUNT_ALL_ASSIGNED_BY_MANAGER;

    @Value("${request.count.all.assigned}")
    private String COUNT_ALL_ASSIGNED;

    @Value("${request.count.by.priority}")
    private String COUNT_WITH_PRIORITY;

    @Value("${request.count.free}")
    private String COUNT_FREE;

    @Value("${request.find.by.request.group}")
    private String GET_REQUESTS_BY_REQUEST_GROUP_ID;

    @Value("${request.count.by.employee}")
    private String COUNT_ALL_REQUEST_BY_EMPLOYEE;

    @Value("${request.count.by.manager}")
    private String COUNT_ALL_REQUEST_BY_MANAGER;

    @Value("${request.count.by.request.group}")
    private String COUNT_REQUEST_BY_REQUEST_GROUP;

    @Value("${request.update.group}")
    private String UPDATE_REQUEST_GROUP;

    @Value("${request.all.per.month}")
    private String GET_ALL_REQUEST_BY_MONTH;

    @Value("${request.all.per.quarter}")
    private String GET_ALL_REQUEST_BY_QR;

    @Value("${request.all.per.year}")
    private String GET_ALL_REQUEST_BY_YEAR;

    @Value("${request.all.per.month.by.manager}")
    private String GET_ALL_BY_MG_REQUEST_BY_MONTH;

    @Value("${request.all.per.quarter.by.manager}")
    private String GET_ALL_BY_MG_REQUEST_BY_QUARTER;

    @Value("${request.all.per.year.by.manager}")
    private String GET_ALL_BY_MG_REQUEST_BY_YEAR;

    @Value("${request.all.by.manager.and.period}")
    private String GET_ALL_BY_MG_REQUEST_BY_PERIOD;

    @Value("${request.find.all.closed.by.employee}")
    private String GET_CLOSED_REQUEST_BY_EMPLOYEE;

    @Value("${request.count.closed.by.employee}")
    private String COUNT_CLOSED_REQUEST_BY_EMPLOYEE;

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
        return getJdbcTemplate().update(UPDATE_REQUEST_STATUS, status.getId(), request.getId());
    }

    @Override
    public int deleteRequest(Request request) {
        return getJdbcTemplate().update(DELETE_REQUEST, request.getId());
    }

    @Override
    public List<Request> getAllRequests() {
        return this.queryForList(FIND_ALL_REQUEST);
    }

    public List<Request> getRequestsByEmployee(Pageable pageable, Person employee) {
        return this.queryForList(GET_ALL_REQUESTS_BY_EMPLOYEE, pageable, employee.getId());
    }

    @Override
    public List<Request> getClosedRequestsByEmployee(Pageable pageable, Person person) {
        return this.queryForList(GET_CLOSED_REQUEST_BY_EMPLOYEE, pageable, person.getId());
    }

    @Override
    public List<Request> getRequestsByEmployeeAndPeriod(Timestamp start, Timestamp end, Person employee) {
        return this.queryForList(GET_ALL_BY_MG_REQUEST_BY_PERIOD, start, end, start, end, employee.getId());
    }

    @Override
    public List<Request> getAllSubRequest(Long parentId) {
        return super.queryForList(FIND_ALL_SUB_REQUEST, parentId);
    }

    @Override
    public List<Request> getAllAssignedRequestByManager(Long managerId, Pageable pageable) {
        return super.queryForList(FIND_ALL_ASSIGNED_BY_MANAGER, pageable, managerId);
    }

    @Override
    public List<Request> getAllAssignedRequestByManager(Long managerId) {
        return super.queryForList(FIND_ALL_ASSIGNED_BY_MANAGER, managerId);
    }

    @Override
    public List<Request> getAllAssignedRequest(Long managerId, Pageable pageable) {
        return super.queryForList(FIND_ALL_ASSIGNED, pageable, managerId);
    }

    @Override
    public List<Request> getAllRequestByUser(Long userId,Pageable pageable) {
        return super.queryForList(FIND_ALL_BY_USER, pageable, userId);
    }

    @Override
    public List<Request> getAllRequestByUser(Long userId) {
        return super.queryForList(FIND_ALL_BY_USER, userId);
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
    public int unassign(Long requestId) {
        return getJdbcTemplate().update(UNASSIGN_REQUEST, requestId);
    }

    @Override
    public Long countFreeByPriority(Integer priorityId) {
        return getJdbcTemplate().queryForObject(COUNT_WITH_PRIORITY, Long.class, priorityId);
    }

    @Override
    public Long countFree() {
        return getJdbcTemplate().queryForObject(COUNT_FREE, Long.class);
    }

    @Override
    public Long countAllByUser(Long userId) {
        return getJdbcTemplate().queryForObject(COUNT_ALL_BY_USER, Long.class, userId);
    }

    @Override
    public Long countAllAssignedByManager(Long managerId) {
        return getJdbcTemplate().queryForObject(COUNT_ALL_ASSIGNED_BY_MANAGER, Long.class, managerId);
    }

    @Override
    public Long countAllAssigned(Long managerId) {
        return getJdbcTemplate().queryForObject(COUNT_ALL_ASSIGNED, Long.class, managerId);
    }

    @Override
    public Long countClosedRequestByEmployee(Long personId) {
        return getJdbcTemplate().queryForObject(COUNT_CLOSED_REQUEST_BY_EMPLOYEE, Long.class, personId);
    }

    @Override
    public Long countAllRequestByEmployee(Long employeeID) {
        return getJdbcTemplate().queryForObject(COUNT_ALL_REQUEST_BY_EMPLOYEE, Long.class, employeeID);
    }

    @Override
    public Long countAllRequestByManager(Long managerID) {
        return getJdbcTemplate().queryForObject(COUNT_ALL_REQUEST_BY_MANAGER, Long.class, managerID);
    }

    @Override
    public Long countRequestsByRequestGroupId(Integer requestGroupId) {
        return getJdbcTemplate().queryForObject(COUNT_REQUEST_BY_REQUEST_GROUP, Long.class, requestGroupId);
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
        if(reportPeriod == null){
            return new ArrayList<>();
        }
        reportPeriod = reportPeriod.toLowerCase();
        return super.queryForList(getQueryByPeriod(reportPeriod, Role.ROLE_EMPLOYEE), personId);
    }

    @Override
    public int updateRequestGroup(Long requestId, Integer requestGroupId) {
        return getJdbcTemplate().update(UPDATE_REQUEST_GROUP, requestGroupId, requestId);
    }

    @Override
    public List<Request> findRequestByEmployeeIdForPeriod(Long personId, String reportPeriod, Pageable pageable) {
        if (reportPeriod == null) {
            return new ArrayList<>();
        }
        reportPeriod = reportPeriod.toLowerCase();
        return super.queryForList(getQueryByPeriod(reportPeriod, Role.ROLE_EMPLOYEE), pageable, personId);
    }

    @Override
    public Long countRequestByEmployeeIdForPeriod(Long personId, String reportPeriod) {
        if (reportPeriod == null) return 0L;
        reportPeriod = reportPeriod.toLowerCase();
        String countQuery = getQueryByPeriod(reportPeriod, Role.ROLE_EMPLOYEE).replace("*", "count(request_id)");
        return getJdbcTemplate().queryForObject(countQuery, Long.class, personId);
    }

    @Override
    public List<Request> findAllAssignedRequestToManagerForPeriod(Long personId, String reportPeriod) {
        if (reportPeriod == null) {
            return new ArrayList<>();
        }
        reportPeriod = reportPeriod.toLowerCase();
        return super.queryForList(getQueryByPeriod(reportPeriod, Role.ROLE_OFFICE_MANAGER), personId);
    }

    @Override
    public Long countAllAssignedRequestToManagerForPeriod(Long personId, String reportPeriod) {
        if (reportPeriod == null) return 0L;
        reportPeriod = reportPeriod.toLowerCase();
        String countQuery = getQueryByPeriod(reportPeriod, Role.ROLE_OFFICE_MANAGER).replace("*", "count(request_id)");
        return getJdbcTemplate().queryForObject(countQuery, Long.class, personId);
    }

    @Override
    public List<Request> findAllAssignedRequestToManagerForPeriod(Long personId, String reportPeriod, Pageable pageable) {
        if (reportPeriod == null) {
            return new ArrayList<>();
        }
        reportPeriod = reportPeriod.toLowerCase();
        return super.queryForList(getQueryByPeriod(reportPeriod, Role.ROLE_OFFICE_MANAGER),pageable, personId);
    }

    @Override
    public int removeRequestFromRequestGroup(Long requestId) {
        return getJdbcTemplate().update(UPDATE_REQUEST_GROUP, null, requestId);
    }

    @Override
    public List<Request> getFreeRequestsWithPriority(Integer priorityId, Pageable pageable, Priority priority) {
        return this.queryForList(GET_AVAILABLE_REQUESTS_BY_PRIORITY, pageable, priorityId);
    }

    @Override
    public List<Request> getFreeRequests(Pageable pageable) {
        return this.queryForList(GET_AVAILABLE_REQUESTS, pageable);
    }

    @Override
    public List<Request> getFreeRequests() {
        return this.queryForList(GET_AVAILABLE_REQUESTS);
    }

    @Override
    public Optional<Request> findOne(Long requestId) {
        return super.findOne(requestId);
    }

    @Override
    public Optional<Request> findSubrequestByIdAndParent(Long id, Long parenId) {
        return this.queryForObject("SELECT * FROM request WHERE request_id  = ? AND parent_id = ?", id, parenId);
    }

    private String getQueryByPeriod(String period, String role) {
        switch (period) {
            case "month":
                return role.equals(Role.ROLE_OFFICE_MANAGER)?GET_ALL_BY_MG_REQUEST_BY_MONTH:GET_ALL_REQUEST_BY_MONTH;
            case "quarter":
                return role.equals(Role.ROLE_OFFICE_MANAGER)?GET_ALL_BY_MG_REQUEST_BY_QUARTER:GET_ALL_REQUEST_BY_QR;
            case "year":
                return role.equals(Role.ROLE_OFFICE_MANAGER)?GET_ALL_BY_MG_REQUEST_BY_YEAR:GET_ALL_REQUEST_BY_YEAR;
            default:
                return "";
        }
    }
}
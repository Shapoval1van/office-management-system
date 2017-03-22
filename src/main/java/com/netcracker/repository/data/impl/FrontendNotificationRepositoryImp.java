package com.netcracker.repository.data.impl;


import com.netcracker.model.entity.FrontendNotification;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;
import com.netcracker.repository.common.GenericJdbcRepository;
import com.netcracker.repository.data.interfaces.FrontendNotificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class FrontendNotificationRepositoryImp  extends GenericJdbcRepository<FrontendNotification, Long>  implements FrontendNotificationRepository{

    @Value("${get.all.notification.to.person}")
    private String GET_ALL_NOTIFICATION_TO_PERSON;
    @Value("${delete.notification.by.id}")
    private String DELETE_NOTIFICATION_BY_ID;
    @Value("${delete.all.notification.to.person}")
    private String DELETE_NOTIFICATION_BY_PERSON;


    public static final String PERSON_ID_COLUMN = "person_id";
    public static final String SUBJECT_COLUMN = "subject";
    public static final String CREATION_TIME = "created";
    public static final String REQUEST_ID_COLUMN = "request_id";

    public FrontendNotificationRepositoryImp() {
        super(FrontendNotification.TABLE_NAME, FrontendNotification.ID_COLUMN);
    }

    @Override
    public Map<String, Object> mapColumns(FrontendNotification entity) {
        Map<String, Object> columns = new HashMap<>();
        columns.put(ID_COLUMN, entity.getId());
        columns.put(PERSON_ID_COLUMN, entity.getPerson().getId());
        columns.put(SUBJECT_COLUMN, entity.getSubject());
        columns.put(CREATION_TIME, entity.getCreationTime());
        columns.put(REQUEST_ID_COLUMN, entity.getRequest().getId());
        return columns;
    }

    @Override
    public RowMapper<FrontendNotification> mapRow() {
        return ((resultSet, i) -> {
            FrontendNotification notification = new FrontendNotification();
            notification.setId(resultSet.getLong(ID_COLUMN));
            notification.setPerson(new Person(resultSet.getLong(PERSON_ID_COLUMN)));
            notification.setSubject(resultSet.getString(SUBJECT_COLUMN));
            notification.setCreationTime(resultSet.getTimestamp(CREATION_TIME));
            notification.setRequest(new Request(resultSet.getLong(REQUEST_ID_COLUMN)));
            return notification;
        });
    }

    @Override
    public List<FrontendNotification> getAllNotificationToPerson(Long personId){
        return super.queryForList(GET_ALL_NOTIFICATION_TO_PERSON, personId);
    }

    @Override
    public int deleteNotificationById(Long notificationId) {
        return super.getJdbcTemplate().update(DELETE_NOTIFICATION_BY_ID, notificationId);
    }

    @Override
    public int deleteAllNotificationByPersonId(Long personId) {
        return super.getJdbcTemplate().update(DELETE_NOTIFICATION_BY_PERSON, personId);
    }
}

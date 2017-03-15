package com.netcracker.repository.data.impl;

import com.netcracker.model.entity.Notification;
import com.netcracker.model.entity.Person;
import com.netcracker.repository.common.GenericJdbcRepository;
import com.netcracker.repository.data.interfaces.NotificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class NotificationRepositoryImpl extends GenericJdbcRepository<Notification, Long> implements NotificationRepository {

    public static final String ID_COLUMN = Notification.ID_COLUMN;
    public static final String PERSON_ID_COLUMN = "person_id";
    public static final String SUBJECT_COLUMN = "subject";
    public static final String TEXT_COLUMN = "notification_text";
    public static final String LINK_COLUMN = "link";

    @Value("${notification.find.all.sort.by.date}")
    public String GET_ALL_NOTIFICATION_SORTED_BY_DATE;

    public NotificationRepositoryImpl() {
        super(Notification.TABLE_NAME, Notification.ID_COLUMN);
    }

    @Override
    public Map<String, Object> mapColumns(Notification entity) {
        Map<String, Object> columns = new HashMap<>();
        columns.put(ID_COLUMN, entity.getId());
        columns.put(PERSON_ID_COLUMN, entity.getPerson().getId());
        columns.put(SUBJECT_COLUMN, entity.getSubject());
        columns.put(TEXT_COLUMN, entity.getText());
        columns.put(LINK_COLUMN, entity.getLink());
        return columns;
    }

    @Override
    public RowMapper<Notification> mapRow() {
        return ((resultSet, i) -> {
            Notification notification = new Notification();
            notification.setId(resultSet.getLong(ID_COLUMN));
            notification.setPerson(new Person(resultSet.getLong(PERSON_ID_COLUMN)));
            notification.setSubject(resultSet.getString(SUBJECT_COLUMN));
            notification.setText(resultSet.getString(TEXT_COLUMN));
            notification.setLink(resultSet.getString(LINK_COLUMN));

            return notification;
        });
    }

    @Override
    public List<Notification> findAllNotificationsSortedByDate() {
        return this.queryForList(GET_ALL_NOTIFICATION_SORTED_BY_DATE);
    }
}

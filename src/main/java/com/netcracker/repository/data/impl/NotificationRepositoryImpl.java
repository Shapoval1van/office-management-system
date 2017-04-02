package com.netcracker.repository.data.impl;

import com.netcracker.model.entity.ChangeItem;
import com.netcracker.model.entity.Notification;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;
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
    public static final String REQUEST_ID_COLUMN = "request_id";
    public static final String TEMPLATE_COLUMN = "template";
    public static final String CHANGE_ITEM_ID_COLUMN = "change_item_id";

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
        columns.put(REQUEST_ID_COLUMN, entity.getRequest().getId());
        columns.put(TEMPLATE_COLUMN, entity.getTemplate());
        columns.put(CHANGE_ITEM_ID_COLUMN, entity.getChangeItem().getId());
        return columns;
    }

    @Override
    public RowMapper<Notification> mapRow() {
        return ((resultSet, i) -> {
            Notification notification = new Notification.NotificationBuilder(
                    new Person(resultSet.getLong(PERSON_ID_COLUMN)), resultSet.getString(SUBJECT_COLUMN))
                    .id(resultSet.getLong(ID_COLUMN))
                    .template(resultSet.getString(TEMPLATE_COLUMN))
                    .text(resultSet.getString(TEXT_COLUMN))
                    .link(resultSet.getString(LINK_COLUMN))
                    .request(new Request(resultSet.getLong(REQUEST_ID_COLUMN)))
                    .changeItem(new ChangeItem(resultSet.getLong(CHANGE_ITEM_ID_COLUMN)))
                    .build();

            return notification;
        });
    }

    @Override
    public List<Notification> findAllNotificationsSortedByDate() {
        return this.queryForList(GET_ALL_NOTIFICATION_SORTED_BY_DATE);
    }
}

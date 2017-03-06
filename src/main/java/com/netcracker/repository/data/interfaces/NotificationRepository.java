package com.netcracker.repository.data.interfaces;


import com.netcracker.model.entity.Notification;
import com.netcracker.repository.common.JdbcRepository;

import java.util.List;

public interface NotificationRepository extends JdbcRepository<Notification, Long> {
    List<Notification> findAllNotificationsSortedByDate();
}

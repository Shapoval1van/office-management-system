package com.netcracker.repository.data.interfaces;


import com.netcracker.model.entity.FrontendNotification;
import com.netcracker.repository.common.JdbcRepository;

import java.util.List;

public interface FrontendNotificationRepository extends JdbcRepository<FrontendNotification, Long> {

    List<FrontendNotification> getAllNotificationToPerson(Long personId);
    int deleteNotificationById(Long notificationId);
    int deleteAllNotificationByPersonId(Long personId);
}

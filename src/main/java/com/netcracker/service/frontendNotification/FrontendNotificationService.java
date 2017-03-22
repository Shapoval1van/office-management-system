package com.netcracker.service.frontendNotification;


import com.netcracker.model.entity.FrontendNotification;

import java.util.List;

public interface FrontendNotificationService {

    List<FrontendNotification> getNotificationToPerson(Long personId);
    int deleteNotificationById(Long id, String personName);
}

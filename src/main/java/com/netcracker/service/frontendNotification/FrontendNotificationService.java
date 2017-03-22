package com.netcracker.service.frontendNotification;


import com.netcracker.model.dto.FrontendNotificationDTO;

import java.util.List;

public interface FrontendNotificationService {

    List<FrontendNotificationDTO> getNotificationToPerson(Long personId);
    int deleteNotificationById(Long id, String personName);
    void sendNotificationToAllSubscribed(Long id, String subject);
}

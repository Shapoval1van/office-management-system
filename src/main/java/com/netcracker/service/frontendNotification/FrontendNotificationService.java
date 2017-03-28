package com.netcracker.service.frontendNotification;


import com.netcracker.exception.CannotDeleteNotificationException;
import com.netcracker.model.dto.FrontendNotificationDTO;
import com.netcracker.model.entity.Request;

import java.security.Principal;
import java.util.List;

public interface FrontendNotificationService {

    List<FrontendNotificationDTO> getNotificationToPerson(Long personId,Principal principal);
    int deleteNotificationById(Long id, String personName) throws CannotDeleteNotificationException;
    int deleteAllNotificationByPersonId(Long id) throws CannotDeleteNotificationException;
    void sendNotificationToAllSubscribed(Request oldRequest, Request newRequest);
}

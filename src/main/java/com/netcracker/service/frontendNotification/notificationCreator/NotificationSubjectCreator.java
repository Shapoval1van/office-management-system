package com.netcracker.service.frontendNotification.notificationCreator;

import com.netcracker.model.entity.Request;

import java.util.Locale;


public interface NotificationSubjectCreator {
    String createNotificationSubject(Request newRequest, Locale locale);
}

package com.netcracker.service.frontendNotification.notificationCreator.subjectCreatorImpl;


import com.netcracker.model.entity.Request;
import com.netcracker.service.frontendNotification.notificationCreator.NotificationSubjectCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static com.netcracker.util.MessageConstant.CHANGE_REQUEST;

@Component
public class SimpleUpdateSubjectCreator implements NotificationSubjectCreator{

    @Autowired
    private MessageSource messageSource;

    @Override
    public String createNotificationSubject(Request newRequest, Locale locale) {
        String requestName = newRequest.getName().toUpperCase();
        return messageSource.getMessage(CHANGE_REQUEST, new Object[]{requestName}, locale);
    }
}

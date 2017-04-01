package com.netcracker.service.frontendNotification.notificationCreator.subjectCreatorImpl;


import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Status;
import com.netcracker.repository.data.interfaces.StatusRepository;
import com.netcracker.service.frontendNotification.notificationCreator.NotificationSubjectCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static com.netcracker.util.MessageConstant.*;

@Component
public class StatusChangeSubjectCreator implements NotificationSubjectCreator {
    int STATUS_CLOSED_ID = 3;
    int STATUS_FREE_ID = 1;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private StatusRepository statusRepository;

    @Override
    public String createNotificationSubject(Request newRequest, Locale locale) {
        String subject;
        Status status = statusRepository.findOne(newRequest.getStatus().getId()).get();
        String statusName = status.getName().toLowerCase().replace("_", " ").toUpperCase();
        String requestName = newRequest.getName().toUpperCase();
        if (status.getId() == STATUS_FREE_ID) {
            subject = messageSource.getMessage(CHANGE_STATUS_TO_FREE, new Object[]{requestName.toUpperCase(), statusName}, locale);
        } else if (status.getId() == STATUS_CLOSED_ID) {
            subject = messageSource.getMessage(CHANGE_STATUS_TO_CLOSED, new Object[]{requestName.toUpperCase(), statusName}, locale);
        } else {
            subject = messageSource.getMessage(CHANGE_STATUS_SUBJECT, new Object[]{requestName.toUpperCase(), statusName}, locale);
        }
        return subject;
    }
}

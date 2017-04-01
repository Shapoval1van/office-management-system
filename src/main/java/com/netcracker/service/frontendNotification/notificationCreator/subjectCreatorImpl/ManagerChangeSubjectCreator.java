package com.netcracker.service.frontendNotification.notificationCreator.subjectCreatorImpl;

import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.service.frontendNotification.notificationCreator.NotificationSubjectCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static com.netcracker.util.MessageConstant.CHANGE_MANGER_SUBJECT;
import static com.netcracker.util.MessageConstant.CHANGE_MANGER_UNASSIGNED;

@Component
public class ManagerChangeSubjectCreator implements NotificationSubjectCreator {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private PersonRepository  personRepository;

    @Override
    public String createNotificationSubject(Request newRequest, Locale locale) {
        String subject;
        String requestName = newRequest.getName().toUpperCase();
        if (newRequest.getManager() == null || newRequest.getManager().getId()==null) {
            // manager was unassigned
            subject = messageSource.getMessage(CHANGE_MANGER_UNASSIGNED, new Object[]{requestName}, locale);
        }else {
            Person manager = personRepository.findOne(newRequest.getManager().getId()).get();
            subject = messageSource.getMessage(CHANGE_MANGER_SUBJECT, new Object[]{requestName, manager.getLastName()}, locale);
        }
        return subject;
    }
}

package com.netcracker.service.frontendNotification.notificationCreator.subjectCreatorImpl;


import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.RequestGroup;
import com.netcracker.repository.data.interfaces.RequestGroupRepository;
import com.netcracker.service.frontendNotification.notificationCreator.NotificationSubjectCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static com.netcracker.util.MessageConstant.CHANGE_GROUP;
import static com.netcracker.util.MessageConstant.CHANGE_GROUP_DELETED;

@Component
public class GroupChangeSubjectCreator implements NotificationSubjectCreator {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private RequestGroupRepository requestGroupRepository;

    @Override
    public String createNotificationSubject(Request newRequest, Locale locale) {
        String requestName = newRequest.getName().toUpperCase();
        String subject;
        if(newRequest.getRequestGroup()==null || newRequest.getRequestGroup().getId()==null){
            //group was removed
            subject = messageSource.getMessage(CHANGE_GROUP_DELETED, new Object[]{requestName.toUpperCase()}, locale);
        }else {
            RequestGroup group = requestGroupRepository.findOne(newRequest.getRequestGroup().getId()).get();
            subject = messageSource.getMessage(CHANGE_GROUP, new Object[]{requestName.toUpperCase(), group.getName()}, locale);
        }
        return subject;
    }
}

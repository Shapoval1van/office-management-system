package com.netcracker.component;

import com.netcracker.model.event.PersonRegistrationEvent;
import com.netcracker.service.notification.impls.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    @Autowired
    private NotificationService notificationService;

    @Async
    @EventListener
    public void handlePersonRegistration(PersonRegistrationEvent event){
        String SITE_LINK = "https://management-office.herokuapp.com/login"; // TODO link to site
        notificationService.sendRegistrationCompletedNotification(event.getPerson(),
                SITE_LINK.concat("/").concat(event.getVerificationToken().getToken()));
    }
}

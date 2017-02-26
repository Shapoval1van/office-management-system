package com.netcracker.component;

import com.netcracker.model.event.PersonRegistrationEvent;
import com.netcracker.model.event.ResetPasswordEvent;
import com.netcracker.service.notification.impls.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    @Autowired
    private NotificationService notificationService;

    @EventListener
    public void handlePersonRegistration(PersonRegistrationEvent event){
        String SITE_LINK = "https://management-office.herokuapp.com/login"; // TODO link to site
        notificationService.sendRegistrationCompletedNotification(event.getPerson(),
                SITE_LINK.concat("/").concat(event.getToken().getTokenValue()));
    }

    @EventListener
    public void handleResetPassword(ResetPasswordEvent event){
        String link = event.getLink().concat("/resetPassword").concat("/"+event.getToken().getTokenValue());
        notificationService.sendPasswordReminder(event.getPerson(), link);
    }
}

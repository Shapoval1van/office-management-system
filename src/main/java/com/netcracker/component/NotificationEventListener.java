package com.netcracker.component;

import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;
import com.netcracker.model.event.*;
import com.netcracker.service.notification.impls.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    @Autowired
    private NotificationService notificationService;

    @EventListener
    public void handlePersonRegistration(PersonRegistrationEvent event) {
        String SITE_LINK = "https://management-office.herokuapp.com/login"; // TODO link to site
        notificationService.sendRegistrationCompletedNotification(event.getPerson(),
                SITE_LINK.concat("/").concat(event.getToken().getTokenValue()));
    }

    @EventListener
    public void handleResetPassword(ResetPasswordEvent event) {
        String link = event.getLink().concat("/resetPassword").concat("/" + event.getToken().getTokenValue());
        notificationService.sendPasswordReminder(event.getPerson(), link);
    }

    @EventListener
    public void handleNewPassword(NewPasswordEvent event) {
        notificationService.sendPasswordForNewManager(event.getPerson());
    }

    @EventListener
    public void handleNotificationSendingError(NotificationSendingErrorEvent event){
        notificationService.saveFailedNotification(event.getNotification());
    }

    @EventListener
    public void handleChangeRequestStatus(NotificationChangeStatus changeStatus){
        notificationService.sendChangeStatusEvent(changeStatus.getPerson());
    }

    @EventListener
    public void handleNewRequest(NotificationNewRequestEvent newRequestEvent){
        notificationService.sendNewRequestEvent(newRequestEvent.getPerson());
    }

    @EventListener
    public void handleUpdateRequest(NotificationRequestUpdateEvent requestUpdateEvent){
        notificationService.sendUpdateRequestEvent(requestUpdateEvent.getPerson());
    }

    @EventListener
    public void handleUpdateUser(NotificationPersonUpdateEvent userUpdateEvent){
        notificationService.sendUpdateUserEvent(userUpdateEvent.getPerson());
    }

    @EventListener
    public void handleRequestExpiring(RequestExpiringEvent event){
        notificationService.sendRequestExpiryReminder(event.getExpiringRequests());
    }
}

package com.netcracker.component;

import com.netcracker.model.event.*;
import com.netcracker.service.frontendNotification.FrontendNotificationService;
import com.netcracker.service.notification.impls.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FrontendNotificationService frontendNotificationService;

    @EventListener
    public void handlePersonRegistration(PersonRegistrationEvent event) {
        notificationService.sendRegistrationCompletedNotification(event.getPerson(), event.getToken().getTokenValue());
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
    public void handleNotificationSendingError(NotificationSendingErrorEvent event) {
        notificationService.saveFailedNotification(event.getNotification());
    }

    @EventListener
    public void handleNewRequest(NotificationNewRequestEvent newRequestEvent) {
        notificationService.sendNewRequestEvent(newRequestEvent.getPerson(), newRequestEvent.getRequest());
    }

    @EventListener
    public void handleDeleteUser(DeleteUserEvent deleteUserEvent){
        notificationService.sendDeleteUserEvent(deleteUserEvent.getPerson());
    }

    @EventListener
    public void handleRecoverUser(RecoverUserEvent recoverUserEvent){
        notificationService.sendRecoverUserEvent(recoverUserEvent.getPerson());
    }

    @EventListener
    public void handleUpdateUser(NotificationPersonUpdateEvent userUpdateEvent) {
        notificationService.sendUpdateUserEvent(userUpdateEvent.getPerson());
    }

    @EventListener
    public void handleRequestExpiring(RequestExpiringEvent event) {
        notificationService.sendRequestExpiryReminder(event.getExpiringRequests());
    }

    @EventListener
    public void handleUpdateRequest(UpdateRequestEvent updateRequestEvent) {
        frontendNotificationService.sendNotificationToAllSubscribed(updateRequestEvent.getOldRequest(),
                updateRequestEvent.getNewRequest());
        notificationService.sendRequestUpdateNotification(updateRequestEvent.getOldRequest(),
                updateRequestEvent.getNewRequest(), updateRequestEvent.getChangeTime());
    }
}

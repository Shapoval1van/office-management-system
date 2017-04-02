package com.netcracker.component;

import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.model.entity.ChangeItem;
import com.netcracker.model.entity.Request;
import com.netcracker.model.event.*;
import com.netcracker.service.frontendNotification.FrontendNotificationService;
import com.netcracker.service.notification.impls.NotificationService;
import com.netcracker.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class NotificationEventListener {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FrontendNotificationService frontendNotificationService;

    @Autowired
    private RequestService requestService;

    @EventListener
    public void handlePersonRegistration(PersonRegistrationEvent event) {
        notificationService.sendRegistrationCompletedNotification(event.getPerson(), event.getToken().getTokenValue());
    }

    @EventListener
    public void handleResetPassword(ResetPasswordEvent event) {
        notificationService.sendPasswordReminder(event.getPerson(), event.getToken().getTokenValue());
    }

    @EventListener
    public void handleNewPassword(NewPasswordEvent event) {
        notificationService.sendRecoveryPasswordForNewUser(event.getPerson(), event.getToken().getTokenValue());
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
    public void handleUpdateRequest(UpdateRequestEvent updateRequestEvent) throws CurrentUserNotPresentException {
        Request oldRequest = updateRequestEvent.getOldRequest();
        Request newRequest = updateRequestEvent.getNewRequest();
        Set<ChangeItem> changes = requestService.updateRequestHistory(newRequest, oldRequest,  updateRequestEvent.getPersonName());
        frontendNotificationService.sendNotificationToAllSubscribed(new Request(oldRequest), new Request(newRequest));
        notificationService.sendRequestUpdateNotification(updateRequestEvent.getOldRequest(), changes);
    }

    @EventListener
    public void handleAssignRequest(RequestAssignEvent requestAssignEvent) throws CurrentUserNotPresentException {
        Request oldRequest = requestAssignEvent.getOldRequest();
        Request newRequest = requestAssignEvent.getNewRequest();
        requestService.updateRequestHistory(new Request(newRequest), new Request(oldRequest),  requestAssignEvent.getPersonName()) ;
        frontendNotificationService.sendNotificationToAllSubscribed(new Request(oldRequest), new Request(newRequest));
        notificationService.sendRequestAssignNotification(newRequest);
    }

    @EventListener
    public void handleUnassignRequest(RequestUnassignEvent requestUnassignEvent) throws CurrentUserNotPresentException {
        Request oldRequest = requestUnassignEvent.getOldRequest();
        Request newRequest = requestUnassignEvent.getNewRequest();
        requestService.updateRequestHistory(new Request(newRequest), new Request(oldRequest),  requestUnassignEvent.getPersonName()) ;
        frontendNotificationService.sendNotificationToAllSubscribed(new Request(oldRequest), new Request(newRequest));
        notificationService.sendRequestUnassignNotification(oldRequest);
    }

    @EventListener
    public void handleAssignRequestToGroup(RequestAddToGroupEvent requestAddToGroupEvent){
        notificationService.requestAssignToGroup(requestAddToGroupEvent.getRequest());
    }

    @EventListener
    public void handleRequestNewComment(RequestNewCommentEvent requestNewCommentEvent){
        notificationService.newComment(requestNewCommentEvent.getRequest());
    }
}

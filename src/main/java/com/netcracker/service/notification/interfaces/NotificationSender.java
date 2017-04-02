package com.netcracker.service.notification.interfaces;

import com.netcracker.model.entity.ChangeItem;
import com.netcracker.model.entity.Notification;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;

import java.util.List;
import java.util.Set;

public interface NotificationSender {
    void sendPasswordReminder(Person person, String link);

    void sendRegistrationCompletedNotification(Person person, String link);

    void sendRecoveryPasswordForNewUser(Person person, String token);

    void sendNewRequestEvent(Person person, Request request);

    void sendUpdateUserEvent(Person person);

    void sendDeleteUserEvent(Person person);

    void sendRecoverUserEvent(Person person);

    void resendNotification();

    void saveFailedNotification(Notification notification);

    void sendRequestExpiryReminder(List<Request> expiringRequests);

    void sendRequestUpdateNotification(Request request, Set<ChangeItem> changes);

    void requestAssignToGroup(Request request);

    void newComment(Request request);

    void sendRequestAssignNotification(Request newRequest);

    void sendRequestUnassignNotification(Request oldRequest);
}

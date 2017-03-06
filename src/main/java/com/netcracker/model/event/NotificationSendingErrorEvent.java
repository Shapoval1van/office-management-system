package com.netcracker.model.event;


import com.netcracker.model.entity.Notification;

public class NotificationSendingErrorEvent {
    private Notification notification;

    public NotificationSendingErrorEvent() {
    }

    public NotificationSendingErrorEvent(Notification notification) {
        this.notification = notification;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}

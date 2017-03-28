package com.netcracker.model.event;


import com.netcracker.model.entity.Request;

public class NotificationRequestUpdateEvent {
    private Request request;

    public NotificationRequestUpdateEvent(){}

    public NotificationRequestUpdateEvent(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}

package com.netcracker.model.event;

import com.netcracker.model.entity.Request;

public class RequestAddToGroupEvent extends NotificationRequestUpdateEvent {
    public RequestAddToGroupEvent(Request request) {
        super(request);
    }
}

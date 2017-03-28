package com.netcracker.model.event;

import com.netcracker.model.entity.Request;

public class RequestAssignEvent extends NotificationRequestUpdateEvent{
    public RequestAssignEvent(Request request) {
        super(request);
    }
}

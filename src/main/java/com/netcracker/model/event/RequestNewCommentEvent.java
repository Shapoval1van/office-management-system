package com.netcracker.model.event;

import com.netcracker.model.entity.Request;

public class RequestNewCommentEvent extends NotificationRequestUpdateEvent{
    public RequestNewCommentEvent(Request request) {
        super(request);
    }
}

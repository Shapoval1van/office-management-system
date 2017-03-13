package com.netcracker.model.event;

import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;

import java.util.List;
import java.util.Map;

public class RequestExpiringEvent {
    private List<Request> expiringRequests;

    public RequestExpiringEvent() {
    }

    public RequestExpiringEvent(List<Request> expiringRequests) {
        this.expiringRequests = expiringRequests;
    }

    public List<Request> getExpiringRequests() {
        return expiringRequests;
    }

    public void setExpiringRequests(List<Request> expiringRequests) {
        this.expiringRequests = expiringRequests;
    }
}

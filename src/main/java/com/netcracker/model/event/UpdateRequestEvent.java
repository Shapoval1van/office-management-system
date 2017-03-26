package com.netcracker.model.event;

import com.netcracker.model.entity.Request;

import java.util.Date;

public class UpdateRequestEvent {
    private Request oldRequest;
    private Request newRequest;
    private Date changeTime;

    public UpdateRequestEvent() {
    }

    public UpdateRequestEvent(Request oldRequest, Request newRequest, Date changeTime) {
        this.oldRequest = oldRequest;
        this.newRequest = newRequest;
        this.changeTime = changeTime;
    }

    public Request getOldRequest() {
        return oldRequest;
    }

    public void setOldRequest(Request oldRequest) {
        this.oldRequest = oldRequest;
    }

    public Request getNewRequest() {
        return newRequest;
    }

    public void setNewRequest(Request newRequest) {
        this.newRequest = newRequest;
    }

    public Date getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Date changeTime) {
        this.changeTime = changeTime;
    }
}

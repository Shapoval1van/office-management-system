package com.netcracker.model.event;

import com.netcracker.model.entity.Request;

import java.util.Date;

public class UpdateRequestEvent {
    private Request oldRequest;
    private Request newRequest;
    private Date changeTime;
    private String personName;

    public UpdateRequestEvent() {
    }

    public UpdateRequestEvent(Request oldRequest, Request newRequest, Date changeTime, String personName) {
        this.oldRequest = oldRequest;
        this.newRequest = newRequest;
        this.changeTime = changeTime;
        this.personName = personName;
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

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }
}

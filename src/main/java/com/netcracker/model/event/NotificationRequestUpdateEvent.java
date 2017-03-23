package com.netcracker.model.event;


import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;

public class NotificationRequestUpdateEvent {
    private Person person;
    private Request request;

    public NotificationRequestUpdateEvent(){}

    public NotificationRequestUpdateEvent(Person person, Request request) {
        this.person = person;
        this.request = request;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}

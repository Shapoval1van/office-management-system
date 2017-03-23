package com.netcracker.model.event;


import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;

public class NotificationChangeStatus {
    private Person person;
    private Request request;
    private String link;

    public NotificationChangeStatus() {
    }

    public NotificationChangeStatus(Person person, Request request) {
        this.person = person;
        this.request = request;
        this.link = "https://management-office.herokuapp.com/request/"+request.getId()+"/details";
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setLink(int requestId) {
        this.link = "http://localhost:8080/request/"+requestId+"/details";
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}


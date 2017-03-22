package com.netcracker.model.entity;


import com.netcracker.repository.common.Persistable;

import java.sql.Timestamp;

public class FrontendNotification implements Persistable<Long> {


    public static final String TABLE_NAME = "frontend_notification";
    public static final String ID_COLUMN = "frontend_notification_id";

    private Long id;
    private Person person;
    private String subject = "";
    private Timestamp creationTime;
    private Request request;

    public FrontendNotification() {
    }

    public FrontendNotification(Person person, String subject, Timestamp creationTime, Request request) {
        this.person = person;
        this.subject = subject;
        this.creationTime = creationTime;
        this.request = request;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}

package com.netcracker.model.entity;

import com.netcracker.repository.common.Persistable;

public class Notification implements Persistable<Long> {

    public static final String TABLE_NAME = "NOTIFICATION";
    public static final String ID_COLUMN = "notification_id";

    private Long id;
    private Person person;
    private String link = "";
    private String text = "";
    private String subject = "";

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}

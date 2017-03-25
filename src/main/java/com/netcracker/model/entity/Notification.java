package com.netcracker.model.entity;

import com.netcracker.repository.common.Persistable;

public class Notification implements Persistable<Long> {

    public static final String TABLE_NAME = "NOTIFICATION";
    public static final String ID_COLUMN = "notification_id";

    private Long id;
    private Person person;
    private String link;
    private String template;
    private String text;
    private String subject;
    private Request request;
    private ChangeItem changeItem;

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

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public ChangeItem getChangeItem() {
        return changeItem;
    }

    public void setChangeItem(ChangeItem changeItem) {
        this.changeItem = changeItem;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

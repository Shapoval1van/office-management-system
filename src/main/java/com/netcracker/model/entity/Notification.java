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

    public Notification() {
    }

    public Notification(NotificationBuilder builder) {
        this.id = builder.id;
        this.person = builder.person;
        this.link = builder.link;
        this.template = builder.template;
        this.text = builder.text;
        this.subject = builder.subject;
        this.request = builder.request;
        this.changeItem = builder.changeItem;
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

    public static class NotificationBuilder {
        private Long id;
        private Person person;
        private String link;
        private String template;
        private String text;
        private String subject;
        private Request request;
        private ChangeItem changeItem;

        public NotificationBuilder(Person person, String subject){
            this.person = person;
            this.subject = subject;
        }

        public NotificationBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public NotificationBuilder link(String link) {
            this.link = link;
            return this;
        }

        public NotificationBuilder template(String template) {
            this.template = template;
            return this;
        }

        public NotificationBuilder text(String text) {
            this.text = text;
            return this;
        }

        public NotificationBuilder request(Request request) {
            this.request = request;
            return this;
        }

        public NotificationBuilder changeItem(ChangeItem changeItem) {
            this.changeItem = changeItem;
            return this;
        }

        public Notification build(){
            return new Notification(this);
        }
    }
}

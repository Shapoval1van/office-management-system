package com.netcracker.model.entity;

import com.netcracker.repository.common.Persistable;

import java.util.Date;

public class Comment implements Persistable<Long> {

    public final static String TABLE_NAME = "COMMENT";
    public final static String ID_COLUMN = "comment_id";

    private Long id;
    private String body;
    private Date publishDate;
    private Person author;
    private Request request;

    public Comment() {
    }

    public Comment(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}

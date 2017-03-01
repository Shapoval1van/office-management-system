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

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (body != null ? body.hashCode() : 0);
        result = 31 * result + (publishDate != null ? publishDate.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (request != null ? request.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null || !(obj instanceof Comment)) return false;

        Comment comment = (Comment) obj;

        if (comment.hashCode() != this.hashCode())
            return false;

        if (id != null ? !id.equals(comment.id) : comment.id != null) return false;
        if (body != null ? !body.equals(comment.body) : comment.body != null) return false;
        if (publishDate != null ? !publishDate.equals(comment.publishDate) : comment.publishDate != null) return false;
        if (author != null ? !author.equals(comment.author) : comment.author != null) return false;
        if (request != null ? !request.equals(comment.request) : comment.request != null) return false;

        return true;
    }
}

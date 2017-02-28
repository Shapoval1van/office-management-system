package com.netcracker.model.dto;

import com.netcracker.model.entity.Comment;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;

import java.util.Date;

public class CommentDTO {

    private Long id;
    private String body;
    private Long author;
    private Long request;
    private Date publishDate;

    public CommentDTO() {
    }

    public CommentDTO(Comment comment) {
        if (comment != null) {
            this.id = comment.getId();
            this.body = comment.getBody();
            if (comment.getAuthor() != null)
                this.author = comment.getAuthor().getId();
            if (comment.getRequest() != null)
                this.request = comment.getRequest().getId();
            this.publishDate = comment.getPublishDate();
        }
    }

    public Comment toComment() {
        Comment comment = new Comment();
        comment.setId(this.id);
        comment.setBody(this.body);
        comment.setPublishDate(this.publishDate);
        comment.setAuthor(new Person(this.author));
        comment.setRequest(new Request(this.request));
        return comment;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getAuthor() {
        return author;
    }

    public void setAuthor(Long author) {
        this.author = author;
    }

    public Long getRequest() {
        return request;
    }

    public void setRequest(Long request) {
        this.request = request;
    }
}

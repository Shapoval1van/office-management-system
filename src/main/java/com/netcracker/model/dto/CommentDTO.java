package com.netcracker.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.model.entity.Comment;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;
import com.netcracker.model.validation.CreateValidatorGroup;
import com.netcracker.model.validation.UpdateValidatorGroup;
import com.netcracker.model.view.View;

import javax.validation.constraints.Size;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentDTO {

    @JsonView(View.Public.class)
    private Long id;
    @JsonView(View.Public.class)
    @Size(min = 1, groups = {CreateValidatorGroup.class, UpdateValidatorGroup.class})
    private String body;
    @JsonView(View.Public.class)
    private Long author;
    @JsonView(View.Public.class)
    private Long request;
    @JsonView(View.Public.class)
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
        if (this.author != null)
            comment.setAuthor(new Person(this.author));
        if (this.request != null)
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

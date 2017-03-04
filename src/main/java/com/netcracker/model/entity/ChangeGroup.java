package com.netcracker.model.entity;

import com.netcracker.repository.common.Persistable;

import java.util.Date;
import java.util.Set;

public class ChangeGroup implements Persistable<Long> {
    public static final String TABLE_NAME = "CHANGE_GROUP";
    public static final String ID_COLUMN = "change_group_id";

    private Long id;
    private Date createDate;
    private Request request;
    private Person author;
    private Set<ChangeItem> changeItems;

    public ChangeGroup() {
    }

    public ChangeGroup(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

    public Set<ChangeItem> getChangeItems() {
        return changeItems;
    }

    public void setChangeItems(Set<ChangeItem> changeItems) {
        this.changeItems = changeItems;
    }
}

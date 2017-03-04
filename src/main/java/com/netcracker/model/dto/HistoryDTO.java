package com.netcracker.model.dto;


import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.model.entity.ChangeGroup;
import com.netcracker.model.entity.ChangeItem;
import com.netcracker.model.view.View;

import java.util.Date;
import java.util.Set;

public class HistoryDTO {

    @JsonView(View.Public.class)
    private Long id;
    @JsonView(View.Public.class)
    private Date createDate;
    @JsonView(View.Public.class)
    private PersonDTO author;
    @JsonView(View.Public.class)
    private Set<ChangeItem> changeItems;

    public HistoryDTO(ChangeGroup changeGroup){
        this.id = changeGroup.getId();
        this.createDate = changeGroup.getCreateDate();
        this.author = new PersonDTO(changeGroup.getAuthor());
        this.changeItems = changeGroup.getChangeItems();
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

    public PersonDTO getAuthor() {
        return author;
    }

    public void setAuthor(PersonDTO author) {
        this.author = author;
    }

    public Set<ChangeItem> getChangeItems() {
        return changeItems;
    }

    public void setChangeItems(Set<ChangeItem> changeItems) {
        this.changeItems = changeItems;
    }
}

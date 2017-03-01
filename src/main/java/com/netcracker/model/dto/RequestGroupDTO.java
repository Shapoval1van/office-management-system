package com.netcracker.model.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.model.entity.RequestGroup;
import com.netcracker.model.view.View;

public class RequestGroupDTO {
    @JsonView(View.Public.class)
    private Integer id;

    @JsonView(View.Public.class)
    private String name;

    public RequestGroupDTO(RequestGroup requestGroup) {
        this.id = requestGroup.getId();
        this.name = requestGroup.getName();
    }

    public RequestGroup toRequestGroup() {
        RequestGroup requestGroup = new RequestGroup();
        requestGroup.setId(this.id);
        requestGroup.setName(this.name);
        return requestGroup;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

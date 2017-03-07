package com.netcracker.model.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.model.entity.Status;
import com.netcracker.model.view.View;

public class StatusDTO {
    @JsonView(View.Public.class)
    private Integer id;

    @JsonView(View.Public.class)
    private String name;

    public StatusDTO() {
    }

    public StatusDTO(Status status) {
        this.id = status.getId();
        this.name = status.getName();
    }

    public Status toStatus() {
        Status status = new Status();
        status.setId(this.id);
        status.setName(this.name);
        return status;
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

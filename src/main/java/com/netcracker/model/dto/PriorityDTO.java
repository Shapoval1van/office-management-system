package com.netcracker.model.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.model.entity.Priority;
import com.netcracker.model.view.View;

public class PriorityDTO {
    @JsonView(View.Public.class)
    private Integer id;

    @JsonView(View.Public.class)
    private String name;

    public PriorityDTO(Priority priority) {
        this.id = priority.getId();
        this.name = priority.getName();
    }

    public Priority toPriority() {
        Priority priority = new Priority();
        priority.setId(this.id);
        priority.setName(this.name);
        return priority;
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

package com.netcracker.model.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.RequestGroup;
import com.netcracker.model.validation.CreateValidatorGroup;
import com.netcracker.model.validation.UpdateValidatorGroup;
import com.netcracker.model.view.View;

import javax.validation.constraints.NotNull;

public class RequestGroupDTO {
    @JsonView(View.Public.class)
    private Integer id;

    @JsonView(View.Public.class)
    @NotNull(groups = {CreateValidatorGroup.class, UpdateValidatorGroup.class})
    private String name;

    @JsonView(View.Public.class)
    private Long author;

    private Long requestCount;

    public RequestGroupDTO() {
    }

    public RequestGroupDTO(RequestGroup requestGroup) {
        this.id = requestGroup.getId();
        this.name = requestGroup.getName();
        if (requestGroup.getAuthor() != null)
            this.author = requestGroup.getAuthor().getId();
    }

    public RequestGroup toRequestGroup() {
        RequestGroup requestGroup = new RequestGroup();
        requestGroup.setId(this.id);
        requestGroup.setName(this.name);
        if (this.author != null)
            requestGroup.setAuthor(new Person(this.author));
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

    public Long getAuthor() {
        return author;
    }

    public void setAuthor(Long author) {
        this.author = author;
    }

    public Long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(Long requestCount) {
        this.requestCount = requestCount;
    }
}

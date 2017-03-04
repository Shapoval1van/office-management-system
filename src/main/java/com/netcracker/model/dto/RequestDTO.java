package com.netcracker.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

import com.netcracker.model.entity.*;
import com.netcracker.model.validation.CreateValidatorGroup;
import com.netcracker.model.validation.UpdateValidatorGroup;
import com.netcracker.model.view.View;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestDTO {

    @JsonView(View.Public.class)
    private Long id;

    @JsonView(View.Public.class)
    @Size(min = 3, max = 50, groups = {CreateValidatorGroup.class, UpdateValidatorGroup.class})
    @NotNull(groups = {CreateValidatorGroup.class, UpdateValidatorGroup.class})
    private String name;

    @JsonView(View.Public.class)
    @Size(max = 500, groups = {CreateValidatorGroup.class, UpdateValidatorGroup.class})
    private String description;

    @JsonView(View.Public.class)
    private Timestamp creationTime;

    @JsonView(View.Public.class)
    private Timestamp estimate;

    @JsonView(View.Public.class)
    private Integer status;

    @JsonView(View.Public.class)
    private Long employee;

    @JsonView(View.Public.class)
    private Long manager;

    @JsonView(View.Public.class)
    private Long parent;

    @JsonView(View.Public.class)
    @NotNull(groups = {CreateValidatorGroup.class, UpdateValidatorGroup.class})
    private Integer priority;

    @JsonView(View.Public.class)
    private Integer requestGroup;

    public RequestDTO() {
    }

    public RequestDTO(Request request) {
        if (request != null) {
            this.id = request.getId();
            this.name = request.getName();
            this.description = request.getDescription();
            this.creationTime = request.getCreationTime();
            this.estimate = request.getEstimate();
            if (request.getStatus() != null) {
                this.status = request.getStatus().getId();
            }
            if(request.getEmployee() != null) {
                this.employee = request.getEmployee().getId();
            }
            if (request.getManager() != null)
                this.manager = request.getManager().getId();
            if (request.getParent() != null)
                this.parent = request.getParent().getId();
            this.priority = request.getPriority().getId();
            if (request.getRequestGroup() != null)
                this.requestGroup = request.getRequestGroup().getId();
        }
    }

    public Request toRequest(){
        Request request = new Request();
        request.setId(this.id);
        request.setName(this.name);
        request.setDescription(this.description);
        request.setCreationTime(this.creationTime);
        if(this.estimate != null) {
            request.setEstimate(this.estimate);
        }
        if(this.status != null) {
            request.setStatus(new Status(this.status));
        }
        if(this.employee != null) {
            request.setEmployee(new Person(this.employee));
        }
        if(this.manager != null) {
            request.setManager(new Person(this.manager));
        }
        if(this.parent != null) {
            request.setParent(new Request(this.parent));
        }
        request.setPriority(new Priority(this.priority));
        if(this.requestGroup != null) {
            request.setRequestGroup(new RequestGroup(this.requestGroup));
        }
        return request;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    public Timestamp getEstimate() {
        return estimate;
    }

    public void setEstimate(Timestamp estimate) {
        this.estimate = estimate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getEmployee() {
        return employee;
    }

    public void setEmployee(Long employee) {
        this.employee = employee;
    }

    public Long getManager() {
        return manager;
    }

    public void setManager(Long manager) {
        this.manager = manager;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getRequestGroup() {
        return requestGroup;
    }

    public void setRequestGroup(Integer requestGroup) {
        this.requestGroup = requestGroup;
    }
}

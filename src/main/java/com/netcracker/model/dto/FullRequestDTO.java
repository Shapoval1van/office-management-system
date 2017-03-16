package com.netcracker.model.dto;


import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.model.entity.*;
import com.netcracker.model.view.View;

import java.sql.Timestamp;

public class FullRequestDTO {
    @JsonView(View.Public.class)
    private Long id;

    @JsonView(View.Public.class)
    private String name;

    @JsonView(View.Public.class)
    private String description;

    @JsonView(View.Public.class)
    private Timestamp creationTime;

    @JsonView(View.Public.class)
    private Timestamp estimate;

    @JsonView(View.Public.class)
    private StatusDTO status;

    @JsonView(View.Public.class)
    private PersonDTO employee;

    @JsonView(View.Public.class)
    private PersonDTO manager;

    @JsonView(View.Public.class)
    private FullRequestDTO parent;

    @JsonView(View.Public.class)
    private PriorityDTO priority;

    @JsonView(View.Public.class)
    private RequestGroupDTO requestGroup;

    public FullRequestDTO(Request request) {
        if (request != null) {
            this.id = request.getId();
            this.name = request.getName();
            this.description = request.getDescription();
            this.creationTime = request.getCreationTime();
            this.estimate = request.getEstimate();
            if (request.getStatus() != null) {
                this.status = new StatusDTO(request.getStatus());
            }
            if(request.getEmployee() != null) {
                this.employee = new PersonDTO(request.getEmployee());
            }
            if (request.getManager() != null)
                this.manager = new PersonDTO(request.getManager());
            if (request.getParent() != null)
                this.parent = new FullRequestDTO(request.getParent());
            if(request.getPriority()!=null){
                this.priority = new PriorityDTO(request.getPriority());
            }
            if (request.getRequestGroup() != null)
                this.requestGroup = new RequestGroupDTO(request.getRequestGroup());
        }
    }

    public Request toRequest(){
        Request request = new Request();
        request.setId(this.id);
        request.setName(this.name);
        request.setDescription(this.description);
        request.setCreationTime(this.creationTime);
        request.setEstimate(this.estimate);
        if(this.status != null) {
            request.setStatus(this.status.toStatus());
        }
        if(this.employee != null) {
            request.setEmployee(this.employee.toPerson());
        }
        if(this.manager != null) {
            request.setManager(this.manager.toPerson());
        }
        if(this.parent != null) {
            request.setParent(this.parent.toRequest());
        }
        request.setPriority(this.priority.toPriority());
        if(this.requestGroup != null) {
            request.setRequestGroup(this.requestGroup.toRequestGroup());
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

    public StatusDTO getStatus() {
        return status;
    }

    public void setStatus(StatusDTO status) {
        this.status = status;
    }

    public PersonDTO getEmployee() {
        return employee;
    }

    public void setEmployee(PersonDTO employee) {
        this.employee = employee;
    }

    public PersonDTO getManager() {
        return manager;
    }

    public void setManager(PersonDTO manager) {
        this.manager = manager;
    }

    public FullRequestDTO getParent() {
        return parent;
    }

    public void setParent(FullRequestDTO parent) {
        this.parent = parent;
    }

    public PriorityDTO getPriority() {
        return priority;
    }

    public void setPriority(PriorityDTO priority) {
        this.priority = priority;
    }

    public RequestGroupDTO getRequestGroup() {
        return requestGroup;
    }

    public void setRequestGroup(RequestGroupDTO requestGroup) {
        this.requestGroup = requestGroup;
    }
}

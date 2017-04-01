package com.netcracker.model.entity;

import com.netcracker.repository.common.Persistable;

import java.sql.Timestamp;

public class Request implements Persistable<Long> {
    public static final String TABLE_NAME = "REQUEST";
    public static final String ID_COLUMN = "request_id";

    private Long id;
    private String name;
    private String description;
    private Timestamp creationTime;
    private Timestamp estimate;
    private Status status;
    private Person employee;
    private Person manager;
    private Request parent;
    private Priority priority;
    private RequestGroup requestGroup;

    public Request() {
    }

    public Request(Long id) {
        this.id = id;
    }

    public Request(Request other) {
        if(other == null) return;
        this.id = other.id;
        this.name = other.name;
        this.description = other.description;
        this.creationTime = other.creationTime;
        this.estimate = other.estimate;
        this.status = other.status;
        this.employee = other.employee==null?null:new Person(other.employee);
        this.manager = other.manager==null?null:new Person(other.manager);
        this.parent = other.parent;
        this.priority = other.priority;
        this.requestGroup = other.requestGroup;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Person getEmployee() {
        return employee;
    }

    public void setEmployee(Person employee) {
        this.employee = employee;
    }

    public Person getManager() {
        return manager;
    }

    public void setManager(Person manager) {
        this.manager = manager;
    }

    public Request getParent() {
        return parent;
    }

    public void setParent(Request parent) {
        this.parent = parent;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public RequestGroup getRequestGroup() {
        return requestGroup;
    }

    public void setRequestGroup(RequestGroup requestGroup) {
        this.requestGroup = requestGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Request request = (Request) o;

        if (id != null ? !id.equals(request.id) : request.id != null) return false;
        if (name != null ? !name.equals(request.name) : request.name != null) return false;
        if (description != null ? !description.equals(request.description) : request.description != null) return false;
        if (creationTime != null ? !creationTime.equals(request.creationTime) : request.creationTime != null)
            return false;
        if (estimate != null ? !estimate.equals(request.estimate) : request.estimate != null) return false;
        if (status != null ? !status.equals(request.status) : request.status != null) return false;
        if (employee != null ? !employee.equals(request.employee) : request.employee != null) return false;
        if (manager != null ? !manager.equals(request.manager) : request.manager != null) return false;
        if (parent != null ? !parent.equals(request.parent) : request.parent != null) return false;
        if (priority != null ? !priority.equals(request.priority) : request.priority != null) return false;
        return requestGroup != null ? requestGroup.equals(request.requestGroup) : request.requestGroup == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (creationTime != null ? creationTime.hashCode() : 0);
        result = 31 * result + (estimate != null ? estimate.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (employee != null ? employee.hashCode() : 0);
        result = 31 * result + (manager != null ? manager.hashCode() : 0);
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (priority != null ? priority.hashCode() : 0);
        result = 31 * result + (requestGroup != null ? requestGroup.hashCode() : 0);
        return result;
    }
}

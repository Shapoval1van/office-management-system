package com.netcracker.model.dto;

/**
 * Created by nuts on 3/19/17.
 */

import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.model.entity.Request;
import com.netcracker.model.view.View;

import java.sql.Timestamp;

public class CalendarItemDTO {
    @JsonView(View.Public.class)
    private String title;
    @JsonView(View.Public.class)
    private Long id;
    @JsonView(View.Public.class)
    private Timestamp start;
    @JsonView(View.Public.class)
    private Timestamp end;
    @JsonView(View.Public.class)
    private StatusDTO status;
    @JsonView(View.Public.class)
    private PriorityDTO priority;

    public CalendarItemDTO() {
    }

    public CalendarItemDTO(Request request) {
        this.title = request.getName();
        this.id = request.getId();
        this.start = request.getCreationTime();
        this.end = request.getEstimate();
        this.status = new StatusDTO(request.getStatus());
        this.priority = new PriorityDTO(request.getPriority());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public StatusDTO getStatus() {
        return status;
    }

    public void setStatus(StatusDTO status) {
        this.status = status;
    }
}

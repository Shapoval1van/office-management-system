package com.netcracker.model.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.model.view.View;

public class RequestAssignDTO {
    @JsonView(View.Public.class)
    private Long requestId;

    @JsonView(View.Public.class)
    private Long personId;

    public RequestAssignDTO() {
    }

    public RequestAssignDTO(Long requestId, Long personId) {
        this.requestId = requestId;
        this.personId = personId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }
}

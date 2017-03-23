package com.netcracker.model.dto;

import com.netcracker.model.validation.CreateValidatorGroup;

import javax.validation.constraints.NotNull;

public class SubscribeDTO {
    private Long subscriberId;

    @NotNull(groups = {CreateValidatorGroup.class})
    private Long requestId;

    public Long getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(Long subscriberId) {
        this.subscriberId = subscriberId;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }
}

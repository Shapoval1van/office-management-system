package com.netcracker.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.model.view.View;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportDTO {

    @JsonView(View.Public.class)
    private String label;

    @JsonView(View.Public.class)
    private Long value;

    public ReportDTO(String label) {
        this.label = label;
        this.value = 0l;
    }

    public ReportDTO(String label, Long value) {
        this.label = label;
        this.value = value;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public Long getValue() {
        return value;
    }

}
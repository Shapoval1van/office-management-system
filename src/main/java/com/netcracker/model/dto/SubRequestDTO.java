package com.netcracker.model.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.netcracker.model.entity.Request;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubRequestDTO extends RequestDTO {

    private String firstName;
    private String lastName;

    public SubRequestDTO() {
    }

    public SubRequestDTO(Request request) {
        super(request);
        this.firstName = request.getEmployee().getFirstName();
        this.lastName = request.getEmployee().getLastName();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}

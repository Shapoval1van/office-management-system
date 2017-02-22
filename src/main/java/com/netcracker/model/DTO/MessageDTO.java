package com.netcracker.model.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDTO {

    private String message;
    private String error;
    private String email;

    public MessageDTO(String message) {
        this.message = message;
    }

    public MessageDTO(String message, String error) {
        this.message = message;
        this.error = error;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
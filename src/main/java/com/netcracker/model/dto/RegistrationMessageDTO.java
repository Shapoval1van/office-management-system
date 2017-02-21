package com.netcracker.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistrationMessageDTO {

    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private String email;
    private String expiration;

    public RegistrationMessageDTO() {
    }

    public RegistrationMessageDTO(String email, Date expiration) {
        this.email = email;
        Format formatter = new SimpleDateFormat(DATE_PATTERN);
        this.expiration = expiration!=null?formatter.format(expiration):null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }
}

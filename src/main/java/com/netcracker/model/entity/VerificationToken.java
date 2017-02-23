package com.netcracker.model.entity;


import com.netcracker.repository.common.Persistable;

import java.util.Date;

public class VerificationToken implements Persistable<Long> {

    public static final String TABLE_NAME = "VERIFICATION_TOKEN";
    public static final String ID_COLUMN = "verification_token_id";

    private Long id;
    private String token;
    private Person person;
    private Date dateExpired;

    public VerificationToken() {
    }

    public VerificationToken(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Date getDateExpired() {
        return dateExpired;
    }

    public void setDateExpired(Date dateExpired) {
        this.dateExpired = dateExpired;
    }
}

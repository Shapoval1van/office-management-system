package com.netcracker.model.entity;


import com.netcracker.repository.common.Persistable;

import java.util.Calendar;
import java.util.Date;

public class PasswordResetToken  implements Persistable<Long> {

    private static final int EXPIRATION = 60 * 24;

    public static final String TABLE_NAME = "PASS_RESET_TOKEN";
    public static final String ID_COLUMN = "pass_reset_token_id";
    public static final String TOKEN_COLUMN = "token";
    public static final String PERSON_ID_COLUMN = "person_id";
    public static final String DATE_EXPIRED_COLUMN = "date_expired";

    private Long id;
    private String token;
    private Person person;
    private Date expiryDate;

    public PasswordResetToken() {
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    public PasswordResetToken(final String token) {
        this.token = token;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    public PasswordResetToken(final String token, final Person person) {
        this.token = token;
        this.person = person;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
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

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    private Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

}

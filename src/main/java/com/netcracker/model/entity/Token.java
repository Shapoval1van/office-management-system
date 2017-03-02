package com.netcracker.model.entity;


import com.netcracker.repository.common.Persistable;

import java.util.Calendar;
import java.util.Date;

public class Token implements Persistable<Long> {

    static final int EXPIRATION = 60 * 24;

    public static final String TABLE_NAME = "TOKEN";
    public static final String ID_COLUMN = "token_id";

    private Long id;
    private String tokenValue;
    private Person person;
    private Date dateExpired;
    private TokenType tokenType;

    public Token() {
        this.dateExpired = calculateExpiryDate(EXPIRATION);
    }

    public Token(TokenType tokenType) {
        this.tokenType = tokenType;
        this.dateExpired = calculateExpiryDate(EXPIRATION);
    }

    public Token(Long id, TokenType tokenType) {
        this.tokenType = tokenType;
        this.id = id;
        this.dateExpired = calculateExpiryDate(EXPIRATION);
    }

    public Token(String token, Person person, TokenType tokenType) {
        this.tokenType = tokenType;
        this.tokenValue = token;
        this.person = person;
        this.dateExpired = calculateExpiryDate(EXPIRATION);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String token) {
        this.tokenValue = token;
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

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    private Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
}

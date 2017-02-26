package com.netcracker.model.event;


import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Token;

public class SecurityEvent {

    private String link;
    private Person person;
    private Token token;

    public SecurityEvent() {
    }

    public SecurityEvent(String link, Person person, Token token) {
        this.link = link;
        this.person = person;
        this.token = token;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}

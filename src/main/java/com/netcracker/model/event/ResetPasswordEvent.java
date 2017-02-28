package com.netcracker.model.event;


import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Token;

public class ResetPasswordEvent extends SecurityEvent{

    public ResetPasswordEvent(String link, Person person, Token token) {
        super(link, person, token);
    }
}

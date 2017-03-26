package com.netcracker.model.event;

import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Token;

public class NewPasswordEvent extends SecurityEvent{
    public NewPasswordEvent(String link, Person person, Token token) {
        super(link, person, token);
    }
}
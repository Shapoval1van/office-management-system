package com.netcracker.model.event;


import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Token;

public class PersonRegistrationEvent extends  SecurityEvent{

    public PersonRegistrationEvent(String link, Person person, Token token) {
        super(link, person, token);
    }
}

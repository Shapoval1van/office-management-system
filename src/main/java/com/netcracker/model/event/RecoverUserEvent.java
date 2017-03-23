package com.netcracker.model.event;

import com.netcracker.model.entity.Person;


public class RecoverUserEvent {
    private Person person;

    public RecoverUserEvent(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
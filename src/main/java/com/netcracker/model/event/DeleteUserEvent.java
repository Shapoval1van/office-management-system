package com.netcracker.model.event;

import com.netcracker.model.entity.Person;


public class DeleteUserEvent {
    private Person person;

    public DeleteUserEvent(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}

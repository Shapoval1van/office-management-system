package com.netcracker.model.event;


import com.netcracker.model.entity.Person;

public class NotificationChangeStatus {
    private Person person;

    public NotificationChangeStatus() {
    }

    public NotificationChangeStatus(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}


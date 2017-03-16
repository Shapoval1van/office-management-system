package com.netcracker.model.event;


import com.netcracker.model.entity.Person;

public class NotificationNewRequestEvent {
    private Person person;

    public NotificationNewRequestEvent(){}

    public NotificationNewRequestEvent(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}

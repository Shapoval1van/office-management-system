package com.netcracker.model.event;


import com.netcracker.model.entity.Person;

public class NotificationRequestUpdateEvent {
    private Person person;

    public NotificationRequestUpdateEvent(){}

    public NotificationRequestUpdateEvent(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}

package com.netcracker.model.event;


import com.netcracker.model.entity.Person;

public class NotificationUserUpdateEvent {
    private Person person;

    public NotificationUserUpdateEvent(){}

    public NotificationUserUpdateEvent(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}

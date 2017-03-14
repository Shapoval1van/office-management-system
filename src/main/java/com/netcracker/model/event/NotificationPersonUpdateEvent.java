package com.netcracker.model.event;


import com.netcracker.model.entity.Person;

public class NotificationPersonUpdateEvent {
    private Person person;

    public NotificationPersonUpdateEvent(){}

    public NotificationPersonUpdateEvent(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}

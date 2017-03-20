package com.netcracker.model.event;


import com.netcracker.model.entity.Person;

public class NotificationChangeStatus {
    private Person person;
    private String link;

    public NotificationChangeStatus() {
    }

    public NotificationChangeStatus(Person person, long requestId) {
        this.person = person;
        this.link = "https://management-office.herokuapp.com/request/"+requestId+"/details";
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setLink(int requestId) {
        this.link = "http://localhost:8080/request/"+requestId+"/details";
    }
}


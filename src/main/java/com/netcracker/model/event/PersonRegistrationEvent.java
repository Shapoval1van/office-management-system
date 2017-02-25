package com.netcracker.model.event;


import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.VerificationToken;

public class PersonRegistrationEvent {

    private String link;
    private Person person;
    private VerificationToken verificationToken;

    public PersonRegistrationEvent() {
    }

    public PersonRegistrationEvent(String link, Person person, VerificationToken verificationToken) {
        this.link = link;
        this.person = person;
        this.verificationToken = verificationToken;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public VerificationToken getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(VerificationToken verificationToken) {
        this.verificationToken = verificationToken;
    }
}

package com.netcracker.util;


import com.netcracker.model.entity.Notification;
import com.netcracker.model.entity.Person;

public class NotificationBuilder {

    public static Notification build(Person person, String subject, String text, String link){
        Notification notification = new Notification();
        notification.setPerson(person);
        notification.setText(text);
        notification.setSubject(subject);
        notification.setLink(link);
        return notification;
    }

    public static Notification build(Person person, String subject, String text){
        return build(person, subject, text, "");
    }

}

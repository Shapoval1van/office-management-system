package com.netcracker.util;


import com.netcracker.model.entity.Notification;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;

public class NotificationBuilder {

    public static Notification build(Person person, String subject, String text, String link){
        Notification notification = new Notification();
        notification.setPerson(person);
        notification.setText(text);
        notification.setSubject(subject);
        notification.setLink(link);
        return notification;
    }

    public static Notification build(Person person, String subject, String text, Request request){
        Notification notification = new Notification();
        notification.setPerson(person);
        notification.setText(text);
        notification.setSubject(subject);
        notification.setRequest(request);
        return notification;
    }

    public static Notification build(Person person, String subject, String text){
        return build(person, subject, text, "");
    }


}

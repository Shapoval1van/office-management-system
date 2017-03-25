package com.netcracker.util;


import com.netcracker.model.entity.ChangeItem;
import com.netcracker.model.entity.Notification;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;

public class NotificationBuilder {

    private static Notification notification;

    public static Notification build(Person person, String subject, String template){
        notification = new Notification();
        notification.setPerson(person);
        notification.setTemplate(template);
        notification.setSubject(subject);
        return notification;
    }

    public static Notification build(Person person, String subject, String template, String link){
        build(person, subject, template);
        notification.setLink(link);
        return notification;
    }

    public static Notification build(Person person, String subject, String template, String link, Request request){
        build(person, subject, template);
        notification.setLink(link);
        notification.setRequest(request);
        return notification;
    }

    public static Notification build(Person person, String subject, String template, String link,
                                     Request request, ChangeItem changeItem){
        build(person, subject, template, link, request);
        notification.setChangeItem(changeItem);
        return notification;
    }

    public static Notification build(Person person, String subject, String template, String body, String link){
        build(person, subject, template, link);
        notification.setText(body);
        return notification;
    }

}

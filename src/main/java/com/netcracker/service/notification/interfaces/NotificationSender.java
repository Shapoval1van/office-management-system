package com.netcracker.service.notification.interfaces;

import com.netcracker.model.entity.Person;

public interface NotificationSender {
    boolean sendPasswordReminder(Person person, String link);

    boolean sendInformationNotification(Person person);

    boolean sendRegistrationCompletedNotification(Person person);
}

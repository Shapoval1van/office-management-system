package com.netcracker.service.notification.interfaces;

import com.netcracker.model.entity.Person;

public interface NotificationSender {
    void sendPasswordReminder(Person person, String link);

    void sendInformationNotification(Person person);

    void sendCustomInformationNotification(Person person);

    void sendRegistrationCompletedNotification(Person person, String link);

    void sendPasswordForNewManager(Person person);
}

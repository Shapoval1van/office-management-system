package com.netcracker.service.notification.impls;

import com.netcracker.model.entity.Person;
import com.netcracker.model.notification.Notification;
import com.netcracker.service.mail.impls.MailService;
import com.netcracker.service.notification.interfaces.NotificationSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Service
@PropertySource("classpath:notification/templates/notificationTemplates.properties")
public class NotificationService implements NotificationSender {
    @Value("${password.reminder.subject}")
    private String PASSWORD_REMINDER_SUBJECT;
    @Value("${information.message.subject}")
    private String INFORMATION_MESSAGE_SUBJECT;
    @Value("${registration.message.subject}")
    private String REGISTRATION_MESSAGE_SUBJECT;
    @Value("${custom.information.message.subject}")
    private String CUSTOM_INFORMATION_MESSAGE_SUBJECT;

    @Value("${password.reminder.message.src}")
    private String PASSWORD_REMINDER_MESSAGE_SRC;
    @Value("${information.message.src}")
    private String INFORMATION_MESSAGE_SRC;
    @Value("${custom.information.message.src}")
    private String CUSTOM_INFORMATION_MESSAGE_SRC;
    @Value("${registration.message.src}")
    private String REGISTRATION_MESSAGE_SRC;



    @Autowired
    private MailService mailService;

    public boolean sendPasswordReminder(Person person, String link) {
        return mailService.send(person.getEmail(), PASSWORD_REMINDER_SUBJECT, Notification.newNotificationBuilder()
                .setNotificationRecipientName(person.getFirstName())
                .setNotificationText(PASSWORD_REMINDER_MESSAGE_SRC)
                .setNotificationLink(link)
                .build()
                .toString());
    }

    public boolean sendInformationNotification(Person person) {
        return mailService.send(person.getEmail(), INFORMATION_MESSAGE_SUBJECT, Notification.newNotificationBuilder()
                .setNotificationRecipientName(person.getFirstName())
                .setNotificationText(INFORMATION_MESSAGE_SRC)
                .build()
                .toString());
    }

    public boolean sendCustomInformationNotification(Person person) {
        return mailService.send(person.getEmail(), CUSTOM_INFORMATION_MESSAGE_SUBJECT, Notification.newNotificationBuilder()
                .setNotificationRecipientName(person.getFirstName())
                .setNotificationText(CUSTOM_INFORMATION_MESSAGE_SRC)
                .build()
                .toString());
    }

    public boolean sendRegistrationCompletedNotification(Person person, String link) {
        return mailService.send(person.getEmail(), REGISTRATION_MESSAGE_SUBJECT, Notification.newNotificationBuilder()
                .setNotificationRecipientName(person.getFirstName())
                .setNotificationText(REGISTRATION_MESSAGE_SRC)
                .setNotificationLink(link)
                .build()
                .toString());
    }
}

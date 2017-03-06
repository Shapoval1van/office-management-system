package com.netcracker.service.notification.impls;

import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Notification;
import com.netcracker.service.mail.impls.MailService;
import com.netcracker.service.notification.interfaces.NotificationSender;
import com.netcracker.util.NotificationBuilder;
import com.netcracker.util.NotificationTextBuilder;
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
        Notification notification = NotificationBuilder.build(person,
                PASSWORD_REMINDER_SUBJECT,
                PASSWORD_REMINDER_MESSAGE_SRC,
                link);

        return mailService.send(notification);
    }

    public boolean sendInformationNotification(Person person) {
        Notification notification = NotificationBuilder.build(person,
                INFORMATION_MESSAGE_SUBJECT,
                INFORMATION_MESSAGE_SRC);

        return mailService.send(notification);
    }

    public boolean sendCustomInformationNotification(Person person) {
        Notification notification = NotificationBuilder.build(person,
                CUSTOM_INFORMATION_MESSAGE_SUBJECT,
                CUSTOM_INFORMATION_MESSAGE_SRC);

        return mailService.send(notification);
    }

    public boolean sendRegistrationCompletedNotification(Person person, String link) {
        Notification notification = NotificationBuilder.build(person,
                REGISTRATION_MESSAGE_SUBJECT,
                REGISTRATION_MESSAGE_SRC,
                link);

        return mailService.send(notification);
    }

    @Override
    public boolean sendPasswordForNewManager(Person person) {
        Notification notification = NotificationBuilder.build(person,
                REGISTRATION_MESSAGE_SUBJECT,
                ", you are welcome at our system. Let's start work!!!\n"+"Your pass: "+person.getPassword());

        return mailService.send(notification);
    }
}

package com.netcracker.service.notification.impls;

import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Notification;
import com.netcracker.service.mail.impls.MailService;
import com.netcracker.service.notification.interfaces.NotificationSender;
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
    @Autowired
    private NotificationTextBuilder notificationTextBuilder;


    public boolean sendPasswordReminder(Person person, String link) {
        Notification notification = new Notification();
        notification.setPerson(person);
        notification.setText(PASSWORD_REMINDER_MESSAGE_SRC);
        notification.setSubject(PASSWORD_REMINDER_SUBJECT);
        notification.setLink(link);

        return mailService.send(person.getEmail(), PASSWORD_REMINDER_SUBJECT, notificationTextBuilder.buildText(notification));
    }

    public boolean sendInformationNotification(Person person) {
        Notification notification = new Notification();
        notification.setPerson(person);
        notification.setText(INFORMATION_MESSAGE_SRC);
        notification.setSubject(INFORMATION_MESSAGE_SUBJECT);

        return mailService.send(person.getEmail(), INFORMATION_MESSAGE_SUBJECT, notificationTextBuilder.buildText(notification));
    }

    public boolean sendCustomInformationNotification(Person person) {
        Notification notification = new Notification();
        notification.setPerson(person);
        notification.setText(CUSTOM_INFORMATION_MESSAGE_SRC);
        notification.setSubject(CUSTOM_INFORMATION_MESSAGE_SUBJECT);

        return mailService.send(person.getEmail(), CUSTOM_INFORMATION_MESSAGE_SUBJECT, notificationTextBuilder.buildText(notification));
    }

    public boolean sendRegistrationCompletedNotification(Person person, String link) {
        Notification notification = new Notification();
        notification.setPerson(person);
        notification.setText(REGISTRATION_MESSAGE_SRC);
        notification.setSubject(REGISTRATION_MESSAGE_SUBJECT);
        notification.setLink(link);

        return mailService.send(person.getEmail(), REGISTRATION_MESSAGE_SUBJECT, notificationTextBuilder.buildText(notification));
    }

    @Override
    public boolean sendPasswordForNewManager(Person person) {
        Notification notification = new Notification();
        notification.setPerson(person);
        notification.setText(", you are welcome at our system. Let's start work!!!\n"+"Your pass: "+person.getPassword());
        notification.setSubject(REGISTRATION_MESSAGE_SUBJECT);

        return  mailService.send(person.getEmail(), REGISTRATION_MESSAGE_SUBJECT, notificationTextBuilder.buildText(notification));
    }
}

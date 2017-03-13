package com.netcracker.service.notification.impls;

import com.netcracker.model.entity.Notification;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;
import com.netcracker.repository.data.interfaces.NotificationRepository;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.service.mail.impls.MailService;
import com.netcracker.service.notification.interfaces.NotificationSender;
import com.netcracker.util.NotificationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@PropertySource("classpath:notification/templates/notificationTemplates.properties")
public class NotificationService implements NotificationSender {

    private static final int RATE = 1800000;

    @Value("${password.reminder.subject}")
    private String PASSWORD_REMINDER_SUBJECT;
    @Value("${information.message.subject}")
    private String INFORMATION_MESSAGE_SUBJECT;
    @Value("${registration.message.subject}")
    private String REGISTRATION_MESSAGE_SUBJECT;
    @Value("${custom.information.message.subject}")
    private String CUSTOM_INFORMATION_MESSAGE_SUBJECT;
    @Value("${status.message.subject}")
    private String REQUEST_STATUS_CHANGE_SUBJECT;
    @Value("${request.expiry.reminder.message.subject}")
    private String REQUEST_EXPIRY_REMINDER_MESSAGE_SUBJECT;


    @Value("${password.reminder.message.src}")
    private String PASSWORD_REMINDER_MESSAGE_SRC;
    @Value("${information.message.src}")
    private String INFORMATION_MESSAGE_SRC;
    @Value("${custom.information.message.src}")
    private String CUSTOM_INFORMATION_MESSAGE_SRC;
    @Value("${registration.message.src}")
    private String REGISTRATION_MESSAGE_SRC;
    @Value("${requestStatus.message.src}")
    private String STATUS_CHANGE_MESSAGE_SRC;
    @Value("${request.expiry.reminder.message.src}")
    private String REQUEST_EXPIRY_REMINDER_MESSAGE_SRC;

    @Autowired
    private MailService mailService;
    private NotificationRepository notificationRepository;
    @Autowired
    private PersonRepository personRepository;

    @Autowired
    public void setNotificationRepository(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void sendPasswordReminder(Person person, String link) {
        Notification notification = NotificationBuilder.build(person,
                PASSWORD_REMINDER_SUBJECT,
                PASSWORD_REMINDER_MESSAGE_SRC,
                link);

        mailService.send(notification);
    }

    @Override
    public void sendInformationNotification(Person person) {
        Notification notification = NotificationBuilder.build(person,
                INFORMATION_MESSAGE_SUBJECT,
                INFORMATION_MESSAGE_SRC);

        mailService.send(notification);
    }

    @Override
    public void sendCustomInformationNotification(Person person) {
        Notification notification = NotificationBuilder.build(person,
                CUSTOM_INFORMATION_MESSAGE_SUBJECT,
                CUSTOM_INFORMATION_MESSAGE_SRC);

        mailService.send(notification);
    }

    @Override
    public void sendRegistrationCompletedNotification(Person person, String link) {
        Notification notification = NotificationBuilder.build(person,
                REGISTRATION_MESSAGE_SUBJECT,
                REGISTRATION_MESSAGE_SRC,
                link);

        mailService.send(notification);
    }


    @Override
    public void sendChangeStatusEvent(Person person){
        Notification notification = NotificationBuilder.build(person,
                REQUEST_STATUS_CHANGE_SUBJECT, STATUS_CHANGE_MESSAGE_SRC);
        mailService.send(notification);
    }

    @Override
    public void sendPasswordForNewManager(Person person) {
        Notification notification = NotificationBuilder.build(person,
                REGISTRATION_MESSAGE_SUBJECT,
                ", you are welcome at our system. Let's start work!!!\n"+"Your pass: "+person.getPassword());
        mailService.send(notification);
    }

    @Override
    @Scheduled(fixedRate = RATE)
    @Transactional
    public void resendNotification() {

        List<Notification> notifications = notificationRepository.findAllNotificationsSortedByDate();
        notifications.forEach(notification -> {
            notificationRepository.delete(notification.getId());
            Optional<Person> personOptional = personRepository.findOne(notification.getPerson().getId());
            if (personOptional.isPresent()){
                notification.setPerson(personOptional.get());
                notification.setId(null);
                mailService.send(notification);
            }
        });

    }

    @Override
    @Transactional
    public void saveFailedNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    /**
     * Expects request has unique id and single manager for it
     *
     * @param expiringRequests list of requests with expiry estimate time
     */
    @Override
    public void sendRequestExpiryReminder(List<Request> expiringRequests) {
        expiringRequests.forEach(request -> {
            Notification notification = NotificationBuilder.build(request.getManager(),
                    REQUEST_EXPIRY_REMINDER_MESSAGE_SUBJECT,
                    REQUEST_EXPIRY_REMINDER_MESSAGE_SRC,
                    request);
            mailService.send(notification);
        });
    }

}

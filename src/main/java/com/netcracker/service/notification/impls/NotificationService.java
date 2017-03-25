package com.netcracker.service.notification.impls;

import com.netcracker.model.entity.ChangeItem;
import com.netcracker.model.entity.Notification;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;
import com.netcracker.repository.data.interfaces.NotificationRepository;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.service.mail.impls.MailService;
import com.netcracker.service.notification.interfaces.NotificationSender;
import com.netcracker.util.ChangeTracker;
import com.netcracker.util.NotificationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@PropertySource("classpath:property/notification/notificationTemplates.properties")
public class NotificationService implements NotificationSender {

    @Autowired
    private ChangeTracker changeTracker;

    private static final int RATE = 1800000;

    @Value("${password.reminder.subject}")
    private String PASSWORD_REMINDER_SUBJECT;
    @Value("${registration.message.subject}")
    private String REGISTRATION_MESSAGE_SUBJECT;
    @Value("${status.message.subject}")
    private String REQUEST_STATUS_CHANGE_SUBJECT;
    @Value("${new.request.message.subject}")
    private String NEW_REQUEST_SUBJECT;
    @Value("${update.request.message.subject}")
    private String REQUEST_UPDATE_SUBJECT;
    @Value("${update.user.message.subject}")
    private String USER_UPDATE_SUBJECT;
    @Value("${delete.user.message.subject}")
    private String USER_DELETE_SUBJECT;
    @Value("${recover.user.message.subject}")
    private String USER_RECOVER_SUBJECT;
    @Value("${request.expiry.reminder.message.subject}")
    private String REQUEST_EXPIRY_REMINDER_MESSAGE_SUBJECT;
    @Value("${update.request.message.subject}")
    private String REQUEST_UPDATE_MESSAGE_SUBJECT;


    @Value("${password.reminder.message.src}")
    private String PASSWORD_REMINDER_MESSAGE_SRC;
    @Value("${registration.message.src}")
    private String REGISTRATION_MESSAGE_SRC;
    @Value("${requestStatus.message.src}")
    private String STATUS_CHANGE_MESSAGE_SRC;
    @Value("${new.request.message.src}")
    private String NEW_REQUEST_MESSAGE_SRC;
    @Value("${update.request.message.src}")
    private String REQUEST_UPDATE_MESSAGE_SRC;
    @Value("${update.user.message.src}")
    private String USER_UPDATE_MESSAGE_SRC;
    @Value("${request.expiry.reminder.message.src}")
    private String REQUEST_EXPIRY_REMINDER_MESSAGE_SRC;
    @Value("${delete.user.message.src}")
    private String USER_DELETE_MESSAGE_SRC;
    @Value("${recover.deleted.user.message.src}")
    private String USER_RECOVER_MESSAGE_SRC;
    @Value("${simple.message.src}")
    private String SIMPLE_MESSAGE_SRC;
    @Value("${server.source}")
    private String SERVER_SOURCE;

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
                SIMPLE_MESSAGE_SRC,
                PASSWORD_REMINDER_MESSAGE_SRC,
                link);

        mailService.send(notification);
    }

    @Override
    public void sendRegistrationCompletedNotification(Person person, String token) {
        Notification notification = NotificationBuilder.build(person,
                REGISTRATION_MESSAGE_SUBJECT,
                SIMPLE_MESSAGE_SRC,
                REGISTRATION_MESSAGE_SRC,
                SERVER_SOURCE.concat("login/").concat(token));
        mailService.send(notification);
    }

    @Override
    public void sendNewRequestEvent(Person person, Request request) {
        Notification notification = NotificationBuilder.build(person,
                NEW_REQUEST_SUBJECT.concat(request.getName()),
                SIMPLE_MESSAGE_SRC,
                NEW_REQUEST_MESSAGE_SRC,
                detailsLink(request.getId()));
        mailService.send(notification);
    }

    @Override
    public void sendUpdateUserEvent(Person person) {
        Notification notification = NotificationBuilder.build(person,
                USER_UPDATE_SUBJECT,
                USER_UPDATE_MESSAGE_SRC,
                SERVER_SOURCE);
        mailService.send(notification);
    }

    @Override
    public void sendDeleteUserEvent(Person person) {
        Notification notification = NotificationBuilder.build(person,
                USER_DELETE_SUBJECT,
                USER_DELETE_MESSAGE_SRC);
        mailService.send(notification);
    }

    @Override
    public void sendRecoverUserEvent(Person person) {
        Notification notification = NotificationBuilder.build(person,
                USER_RECOVER_SUBJECT,
                USER_RECOVER_MESSAGE_SRC);
        mailService.send(notification);
    }

    @Override
    public void sendPasswordForNewManager(Person person) {
        Notification notification = NotificationBuilder.build(person,
                REGISTRATION_MESSAGE_SUBJECT,
                ", you are welcome at our system. Let's start work!!!\n" + "Your pass: " + person.getPassword());
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
            if (personOptional.isPresent()) {
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
                    REQUEST_EXPIRY_REMINDER_MESSAGE_SUBJECT.concat(request.getName()),
                    REQUEST_EXPIRY_REMINDER_MESSAGE_SRC,
                    detailsLink(request.getId()),
                    request);
            mailService.send(notification);
        });
    }

    @Override
    public void sendRequestUpdateNotification(Request oldRequest, Request newRequest, Date changeTime) {
        List<Person> subscribers = personRepository.findPersonsBySubscribingRequest(oldRequest.getId());
        Set<ChangeItem> changeItemSet = changeTracker.findMismatching(oldRequest, newRequest);

        subscribers.forEach(person -> {
            String requestUpdateMessageSubject = REQUEST_UPDATE_MESSAGE_SUBJECT.concat(oldRequest.getName());

            changeItemSet.forEach(changeItem -> {
                Notification notification = NotificationBuilder.build(person, requestUpdateMessageSubject,
                        REQUEST_UPDATE_MESSAGE_SRC, detailsLink(oldRequest.getId()), oldRequest,
                        new ChangeItem(changeItem.getOldVal(), changeItem.getNewVal(), changeItem.getField()));
                mailService.send(notification);
            });
        });
    }

    private String detailsLink(Long requestId) {
        return new StringBuilder()
                .append(SERVER_SOURCE)
                .append("secured/")
                .append("request/")
                .append(requestId)
                .append("/details")
                .toString();
    }

}

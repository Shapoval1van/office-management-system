package com.netcracker.service.notification.impls;

import com.netcracker.model.entity.*;
import com.netcracker.repository.data.interfaces.*;
import com.netcracker.service.mail.impls.MailService;
import com.netcracker.service.notification.interfaces.NotificationSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@PropertySource("classpath:property/notification/notificationTemplates.properties")
public class NotificationService implements NotificationSender {

    private static final int RATE = 1800000;

    @Value("${password.reminder.subject}")
    private String PASSWORD_REMINDER_SUBJECT;
    @Value("${registration.message.subject}")
    private String REGISTRATION_MESSAGE_SUBJECT;
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
    @Value("${confirmation.of.registration.subject}")
    private String CONFIRMATION_OF_REGISTRATION_SUBJECT;
    @Value("${request.assigned.subject}")
    private String REQUEST_ASSIGNED_SUBJECT;
    @Value("${request.unassigned.subject}")
    private String REQUEST_UNASSIGNED_SUBJECT;
    @Value("${request.assigned.to.group.subject}")
    private String REQUEST_ASSIGNED_TO_GROUP_SUBJECT;
    @Value("${request.new.comment.subject}")
    private String REQUEST_NEW_COMMENT_SUBJECT;

    @Value("${confirmation.of.registration.body}")
    private String CONFIRMATION_OF_REGISTRATION_BODY;
    @Value("${delete.user.message.body}")
    private String USER_DELETE_MESSAGE_BODY;
    @Value("${registration.message.body}")
    private String REGISTRATION_MESSAGE_BODY;
    @Value("${new.request.message.body}")
    private String NEW_REQUEST_MESSAGE_BODY;
    @Value("${recover.deleted.user.message.body}")
    private String USER_RECOVER_MESSAGE_BODY;
    @Value("${password.reminder.message.body}")
    private String PASSWORD_REMINDER_MESSAGE_BODY;
    @Value("${request.assigned.body}")
    private String REQUEST_ASSIGNED_BODY;
    @Value("${request.unassigned.body}")
    private String REQUEST_UNASSIGNED_BODY;
    @Value("${request.assigned.to.group.body}")
    private String REQUEST_ASSIGNED_TO_GROUP_BODY;
    @Value("${request.new.comment.body}")
    private String REQUEST_NEW_COMMENT_BODY;

    @Value("${request.expiry.reminder.message.src}")
    private String REQUEST_EXPIRY_REMINDER_MESSAGE_TEMPLATE;
    @Value("${update.request.message.src}")
    private String REQUEST_UPDATE_MESSAGE_TEMPLATE;
    @Value("${update.user.message.src}")
    private String USER_UPDATE_MESSAGE_TEMPLATE;
    @Value("${simple.message.src}")
    private String SIMPLE_MESSAGE_TEMPLATE;
    @Value("${simple.message.no.btn.src}")
    private String SIMPLE_MESSAGE_NO_BTN_TEMPLATE;
    @Value("${server.source}")
    private String SERVER_SOURCE;

    private MailService mailService;
    private PersonRepository personRepository;
    private NotificationRepository notificationRepository;
    private RequestRepository requestRepository;
    private ChangeItemRepository changeItemRepository;
    private FieldRepository fieldRepository;

    @Autowired
    public NotificationService(MailService mailService, PersonRepository personRepository,
                               NotificationRepository notificationRepository, RequestRepository requestRepository,
                               ChangeItemRepository changeItemRepository, FieldRepository fieldRepository) {
        this.mailService = mailService;
        this.personRepository = personRepository;
        this.notificationRepository = notificationRepository;
        this.requestRepository = requestRepository;
        this.changeItemRepository = changeItemRepository;
        this.fieldRepository = fieldRepository;
    }

    @Override
    public void sendPasswordReminder(Person person, String token) {
        Notification notification = new Notification.NotificationBuilder(person, PASSWORD_REMINDER_SUBJECT)
                .template(SIMPLE_MESSAGE_TEMPLATE)
                .text(PASSWORD_REMINDER_MESSAGE_BODY)
                .link(SERVER_SOURCE.concat("resetPassword/").concat(token))
                .build();
        mailService.send(notification);
    }

    @Override
    public void sendRegistrationCompletedNotification(Person person, String token) {
        Notification notification = new Notification.NotificationBuilder(person, REGISTRATION_MESSAGE_SUBJECT)
                .template(SIMPLE_MESSAGE_TEMPLATE)
                .text(REGISTRATION_MESSAGE_BODY)
                .link(SERVER_SOURCE.concat("login/").concat(token))
                .build();
        mailService.send(notification);
    }

    @Override
    public void sendNewRequestEvent(Person person, Request request) {
        Notification notification = new Notification.NotificationBuilder(person, NEW_REQUEST_SUBJECT.concat(request.getName()))
                .template(SIMPLE_MESSAGE_TEMPLATE)
                .text(NEW_REQUEST_MESSAGE_BODY)
                .link(detailsLink(request.getId()))
                .build();
        mailService.send(notification);
    }

    @Override
    public void sendUpdateUserEvent(Person person) {
        Notification notification = new Notification.NotificationBuilder(person, USER_UPDATE_SUBJECT)
                .template(USER_UPDATE_MESSAGE_TEMPLATE)
                .link(SERVER_SOURCE)
                .build();
        mailService.send(notification);
    }

    @Override
    public void sendDeleteUserEvent(Person person) {
        Notification notification = new Notification.NotificationBuilder(person, USER_DELETE_SUBJECT)
                .template(SIMPLE_MESSAGE_NO_BTN_TEMPLATE)
                .text(USER_DELETE_MESSAGE_BODY)
                .build();
        mailService.send(notification);
    }

    @Override
    public void sendRecoverUserEvent(Person person) {
        Notification notification = new Notification.NotificationBuilder(person, USER_RECOVER_SUBJECT)
                .template(SIMPLE_MESSAGE_TEMPLATE)
                .text(USER_RECOVER_MESSAGE_BODY)
                .link(SERVER_SOURCE)
                .build();
        mailService.send(notification);
    }

    @Override
    public void sendRecoveryPasswordForNewUser(Person person, String token) {
        Notification notification = new Notification.NotificationBuilder(person, CONFIRMATION_OF_REGISTRATION_SUBJECT)
                .template(SIMPLE_MESSAGE_TEMPLATE)
                .text(CONFIRMATION_OF_REGISTRATION_BODY)
                .link(SERVER_SOURCE.concat("resetPassword/").concat(token))
                .build();
        mailService.send(notification);
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
            Notification notification = new Notification.NotificationBuilder(request.getManager(),
                    REQUEST_EXPIRY_REMINDER_MESSAGE_SUBJECT.concat(request.getName()))
                    .template(REQUEST_EXPIRY_REMINDER_MESSAGE_TEMPLATE)
                    .request(request)
                    .link(detailsLink(request.getId()))
                    .build();
            mailService.send(notification);
        });
    }

    @Override
    public void sendRequestUpdateNotification(Request request, Set<ChangeItem> changes) {
        List<Person> subscribers = personRepository.findPersonsBySubscribingRequest(request.getId());
        subscribers.forEach(person -> {
            changes.forEach(changeItem -> {
                Notification notification = new Notification.NotificationBuilder(person,
                                REQUEST_UPDATE_SUBJECT.concat(request.getName()))
                        .template(REQUEST_UPDATE_MESSAGE_TEMPLATE)
                        .link(detailsLink(request.getId()))
                        .request(request)
                        .changeItem(changeItem)
                        .build();
                mailService.send(notification);
            });
        });
    }

    @Override
    public void requestAssignToGroup(Request request) {
        List<Person> subscribers = personRepository.findPersonsBySubscribingRequest(request.getId());
        subscribers.forEach(person -> {
            Notification notification = new Notification.NotificationBuilder(person,
                    REQUEST_ASSIGNED_TO_GROUP_SUBJECT.concat(request.getName()))
                    .template(SIMPLE_MESSAGE_TEMPLATE)
                    .text(REQUEST_ASSIGNED_TO_GROUP_BODY.concat(request.getRequestGroup().getName()))
                    .link(detailsLink(request.getId()))
                    .build();
            mailService.send(notification);
        });
    }

    @Override
    public void newComment(Request request) {
        List<Person> subscribers = personRepository.findPersonsBySubscribingRequest(request.getId());
        subscribers.forEach(person -> {
            Notification notification = new Notification.NotificationBuilder(person,
                    REQUEST_NEW_COMMENT_SUBJECT.concat(request.getName()))
                    .template(SIMPLE_MESSAGE_TEMPLATE)
                    .text(REQUEST_NEW_COMMENT_BODY)
                    .link(detailsLink(request.getId()))
                    .build();
            mailService.send(notification);
        });
    }


    @Override
    public void sendRequestAssignNotification(Request newRequest) {
        List<Person> subscribers = personRepository.findPersonsBySubscribingRequest(newRequest.getId());
        subscribers.forEach(person -> {
            Notification notification = new Notification.NotificationBuilder(person,
                    REQUEST_ASSIGNED_SUBJECT.concat(newRequest.getName()))
                    .template(SIMPLE_MESSAGE_TEMPLATE)
                    .text(REQUEST_ASSIGNED_BODY.concat(newRequest.getManager().getFullName()))
                    .link(detailsLink(newRequest.getId()))
                    .build();
            mailService.send(notification);
        });

    }

    @Override
    public void sendRequestUnassignNotification(Request oldRequest) {
        List<Person> subscribers = personRepository.findPersonsBySubscribingRequest(oldRequest.getId());
        subscribers.forEach(person -> {
            Notification notification = new Notification.NotificationBuilder(person,
                    REQUEST_UNASSIGNED_SUBJECT.concat(oldRequest.getName()))
                    .template(SIMPLE_MESSAGE_TEMPLATE)
                    .text(REQUEST_UNASSIGNED_BODY.concat(oldRequest.getManager().getFullName()))
                    .link(detailsLink(oldRequest.getId()))
                    .build();
            mailService.send(notification);
        });
    }

    @Override
    @Scheduled(fixedRate = RATE)
    @Transactional
    public void resendNotification() {
        List<Notification> notifications = notificationRepository.findAllNotificationsSortedByDate();
        notifications.forEach((Notification notification) -> {
            notificationRepository.delete(notification.getId());
            Optional<Person> personOptional = personRepository.findOne(notification.getPerson().getId());
            Optional<Request> request = requestRepository.findOne(notification.getRequest().getId());
            Optional<ChangeItem> changeItem = changeItemRepository.findOne(notification.getChangeItem().getId());
            if (personOptional.isPresent()) { // If person does not exist than miss sending
                notification.setPerson(personOptional.get());
                notification.setId(null);
                request.ifPresent(notification::setRequest);
                if (changeItem.isPresent()){
                    notification.setChangeItem(changeItem.get());
                    Optional<Field> fieldOptional = fieldRepository.findOne(notification.getChangeItem().getField().getId());
                    fieldOptional.ifPresent(field -> notification.getChangeItem().setField(field));
                }
                changeItem.ifPresent(notification::setChangeItem);
                mailService.send(notification);
            }
        });
    }

    private String detailsLink(Long requestId) {
        return new StringBuilder()
                .append(SERVER_SOURCE)
                .append("secured/")
                .append("employee/")
                .append("request/")
                .append(requestId)
                .append("/details")
                .toString();
    }
}

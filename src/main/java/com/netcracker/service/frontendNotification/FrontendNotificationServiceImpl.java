package com.netcracker.service.frontendNotification;


import com.netcracker.exception.CannotDeleteNotificationException;
import com.netcracker.model.dto.FrontendNotificationDTO;
import com.netcracker.model.entity.ChangeItem;
import com.netcracker.model.entity.FrontendNotification;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;
import com.netcracker.repository.data.interfaces.FrontendNotificationRepository;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.service.frontendNotification.notificationCreator.NotificationSubjectCreator;
import com.netcracker.service.frontendNotification.notificationCreator.NotificationSubjectCreatorFactory;
import com.netcracker.service.frontendNotification.notificationCreator.subjectCreatorImpl.SimpleUpdateSubjectCreator;
import com.netcracker.util.ChangeTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.netcracker.util.MessageConstant.DELETE_NOTIFICATION_EXCEPTION;


@Service
public class FrontendNotificationServiceImpl implements FrontendNotificationService {

    private final Locale locale = LocaleContextHolder.getLocale();
    private final String NOTIFICATION_DESTINATION = "/queue/notification";

    @Autowired
    private FrontendNotificationRepository frontendNotificationRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private NotificationSubjectCreatorFactory notificationSubjectFactory;

    @Autowired
    private ChangeTracker changeTracker;

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated() and  @customExpressionServiceImpl.isPersonIdEqualsToPrincipalPersonId(#id, #principal)")
    public List<FrontendNotificationDTO> getNotificationToPerson(@P("id") Long personId, @P("principal") Principal principal) {
        List<FrontendNotificationDTO> frontendNotificationDTOList = new ArrayList<>();
        frontendNotificationRepository.getAllNotificationToPerson(personId).forEach(frontendNotification ->
                frontendNotificationDTOList.add(new FrontendNotificationDTO(frontendNotification)));
        return frontendNotificationDTOList;
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated() and @customExpressionServiceImpl.isNotificationBelongToPerson(#id, #name)")
    public int deleteNotificationById(@P("id") Long id, @P("name") String personName) throws CannotDeleteNotificationException {
        int result = frontendNotificationRepository.deleteNotificationById(id);
        if (result <= 0)
            throw new CannotDeleteNotificationException(messageSource.getMessage(DELETE_NOTIFICATION_EXCEPTION, null, locale));
        return result;
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public int deleteAllNotificationByPersonId(Long id) throws CannotDeleteNotificationException {
        int result = frontendNotificationRepository.deleteAllNotificationByPersonId(id);
        if (result <= 0)
            throw new CannotDeleteNotificationException(messageSource.getMessage(DELETE_NOTIFICATION_EXCEPTION, null, locale));
        return result;
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void sendNotificationToAllSubscribed(Request oldRequest, Request newRequest) {



        List<Person> people = personRepository.findPersonsBySubscribingRequest(oldRequest.getId());
        ArrayList<ChangeItem> changeItemSet = new ArrayList<>(changeTracker.findMismatching(oldRequest, newRequest));
        if (people.size() == 0) return;
        if (changeItemSet.size() == 0) return;

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        List<FrontendNotification> notifications = new ArrayList<>();
        people.forEach(person -> {
            final boolean[] isSimpleUpdateNotificationAlreadyExist = {false};
            changeItemSet.forEach(changeItem -> {
                NotificationSubjectCreator subjectCreator = notificationSubjectFactory.getNotificationSubjectCreator(changeItem.getField().getId());
                if(!(subjectCreator instanceof SimpleUpdateSubjectCreator)){
                    String subject = subjectCreator.createNotificationSubject(newRequest, locale);
                    notifications.add(new FrontendNotification(person, subject, timestamp, new Request(newRequest.getId())));
                }else if(!isSimpleUpdateNotificationAlreadyExist[0]){
                    String subject = subjectCreator.createNotificationSubject(newRequest, locale);
                    notifications.add(new FrontendNotification(person, subject, timestamp, new Request(newRequest.getId())));
                    isSimpleUpdateNotificationAlreadyExist[0] = true;
                }}
            );
        });

        List<FrontendNotification> savedNotification = new ArrayList<>();
        //save notification
        notifications.forEach(n -> savedNotification.add(frontendNotificationRepository.save(n).get()));
        //send notification
        savedNotification.forEach(n -> simpMessagingTemplate.convertAndSendToUser(n.getPerson().getEmail(),
                NOTIFICATION_DESTINATION, new FrontendNotificationDTO(n)));
    }
}

package com.netcracker.service.frontendNotification;


import com.netcracker.model.dto.FrontendNotificationDTO;
import com.netcracker.model.entity.FrontendNotification;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;
import com.netcracker.repository.data.interfaces.FrontendNotificationRepository;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


@Service
public class FrontendNotificationServiceImpl implements FrontendNotificationService {

    @Autowired
    private FrontendNotificationRepository frontendNotificationRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public List<FrontendNotificationDTO> getNotificationToPerson(Long personId) {
        List<FrontendNotificationDTO> frontendNotificationDTOList = new ArrayList<>();
        frontendNotificationRepository.getAllNotificationToPerson(personId).forEach(frontendNotification ->
                frontendNotificationDTOList.add(new FrontendNotificationDTO(frontendNotification)));
        return  frontendNotificationDTOList;
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated() and @customExpressionServiceImpl.isNotificationBelongToPerson(#id, #name)")
    public int deleteNotificationById(@P("id") Long id, @P("name") String personName) {
        return frontendNotificationRepository.deleteNotificationById(id);
    }

    @Override
    public void sendNotificationToAllSubscribed(Long id, String subject) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        List<Person> people = personRepository.findPersonsBySubscribingRequest(id);
        List<FrontendNotification> notifications = new ArrayList<>();
        for (Person person : people) {
            notifications.add(new FrontendNotification(person, subject, timestamp, new Request(id)));
        }
        for (FrontendNotification notification : notifications) {
            frontendNotificationRepository.save(notification);
        }
        FrontendNotification baseNotification = notifications.get(1);
        baseNotification.setRequest(requestRepository.findOne(id).get());
        FrontendNotificationDTO notificationDTO = new FrontendNotificationDTO();
        people.forEach(person -> simpMessagingTemplate.convertAndSendToUser(person.getEmail(), "/queue/notification", notificationDTO));
    }


}

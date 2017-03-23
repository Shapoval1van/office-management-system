package com.netcracker.service.frontendNotification;


import com.netcracker.exception.CannotDeleteNotificationException;
import com.netcracker.model.dto.FrontendNotificationDTO;
import com.netcracker.model.entity.FrontendNotification;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;
import com.netcracker.repository.data.interfaces.FrontendNotificationRepository;
import com.netcracker.repository.data.interfaces.PersonRepository;
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
    public int deleteNotificationById(@P("id") Long id, @P("name") String personName) throws CannotDeleteNotificationException {
        int result = frontendNotificationRepository.deleteNotificationById(id);
        if(result<=0) throw new CannotDeleteNotificationException("Notification by  id: " + id+" has not deleted");
        return result;
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public int deleteNotificationByPersonId(Long id) throws CannotDeleteNotificationException {
        int result  = frontendNotificationRepository.deleteAllNotificationByPersonId(id);
        if(result<=0) throw new CannotDeleteNotificationException("Notification by person id: " + id+" has not deleted" );
        return result;
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void sendNotificationToAllSubscribed(Long id, String subject) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        List<Person> people = personRepository.findPersonsBySubscribingRequest(id);
        if(people.size()==0) return;
        List<FrontendNotification> notifications = new ArrayList<>();
        for (Person person : people) {
            notifications.add(new FrontendNotification(person, subject, timestamp, new Request(id)));
        }

        List<FrontendNotification> savedNotification = new ArrayList<>();
        for (FrontendNotification notification : notifications) {
            savedNotification.add(frontendNotificationRepository.save(notification).get());
        }

        for (int i = 0; i< people.size(); i++) {
            simpMessagingTemplate.convertAndSendToUser(people.get(i).getEmail(), "/queue/notification", new FrontendNotificationDTO(savedNotification.get(i)));
        }
    }


}

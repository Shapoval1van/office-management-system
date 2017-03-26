package com.netcracker.service.security.customExpression;

import com.netcracker.model.entity.FrontendNotification;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;
import com.netcracker.repository.data.interfaces.FrontendNotificationRepository;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Objects;

@Service
public class CustomExpressionServiceImpl implements CustomExpressionService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private FrontendNotificationRepository frontendNotificationRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean isRequestPermittedToDelete(long requestId) {
        int STATUS_FREE_ID = 1;
        Request request = requestRepository.findOne(requestId).get();
        return request.getStatus().getId()==STATUS_FREE_ID;
    }

    @Override
    public boolean isNotificationBelongToPerson(Long notificationId, String personName) {
        Person person = personRepository.findPersonByEmail(personName).get();
        FrontendNotification frontendNotification = frontendNotificationRepository.findOne(notificationId).get();
        return Objects.equals(frontendNotification.getPerson().getId(), person.getId());
    }

    @Override
    public boolean isPersonIdEqualsToPrincipalPersonId(Long personId, Principal principal) {
        Person person = personRepository.findPersonByEmail(principal.getName()).get();
        return Objects.equals(person.getId(), personId);
    }


}

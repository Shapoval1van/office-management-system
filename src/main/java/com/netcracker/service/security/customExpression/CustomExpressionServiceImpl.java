package com.netcracker.service.security.customExpression;

import com.netcracker.model.entity.*;
import com.netcracker.repository.data.interfaces.FrontendNotificationRepository;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.RequestRepository;
import com.netcracker.repository.data.interfaces.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Objects;

@Service
public class CustomExpressionServiceImpl implements CustomExpressionService {

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private FrontendNotificationRepository frontendNotificationRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean isRequestPermittedToDelete(long requestId) {
        Request request = requestRepository.findOne(requestId).get();
        Status status = statusRepository.findOne(request.getStatus().getId()).get();
        return "FREE".equals(status.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRequestPermittedToChangeStatus(Request request, Status status, Principal principal) {
        Request oldRequest = requestRepository.findOne(request.getId()).get();
        Status oldStatus = statusRepository.findOne(oldRequest.getStatus().getId()).get();
        if("CANCELED".equals(oldStatus.getName())){
           return false;
        }
        return  true;
    }

    @Override
    public boolean isNotificationBelongToPerson(Long notificationId, String personName) {
        Person person = personRepository.findPersonByEmail(personName).get();
        FrontendNotification frontendNotification = frontendNotificationRepository.findOne(notificationId).get();
        return Objects.equals(frontendNotification.getPerson().getId(), person.getId());
    }


}

package com.netcracker.service.security.customExpression;


import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Status;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.RequestRepository;
import com.netcracker.repository.data.interfaces.StatusRepository;
import com.netcracker.repository.data.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
public class CustomExpressionServiceImpl implements CustomExpressionService {

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PersonRepository personRepository;

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
}

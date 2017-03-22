package com.netcracker.service.frontendNotification;


import com.netcracker.model.entity.FrontendNotification;
import com.netcracker.repository.data.interfaces.FrontendNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class FrontendNotificationServiceImpl implements FrontendNotificationService{

//    @Autowired
//    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private FrontendNotificationRepository frontendNotificationRepository;

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public List<FrontendNotification> getNotificationToPerson(Long personId) {
        return frontendNotificationRepository.getAllNotificationToPerson(personId);
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated() and @customExpressionServiceImpl.isNotificationBelongToPerson(#id, #name)")
    public int deleteNotificationById(@P("id")Long id, @P("name")String personName) {
        return frontendNotificationRepository.deleteNotificationById(id);
    }
}

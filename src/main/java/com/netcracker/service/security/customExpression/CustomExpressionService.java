package com.netcracker.service.security.customExpression;


import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Status;

import java.security.Principal;

public interface CustomExpressionService {

    boolean isRequestPermittedToDelete(long requestId);
    boolean isRequestPermittedToChangeStatus(Request request, Status status, Principal principal);
    boolean isNotificationBelongToPerson(Long notificationId, String personName);
}

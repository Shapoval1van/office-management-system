package com.netcracker.service.security.customExpression;


import java.security.Principal;

public interface CustomExpressionService {

    boolean isRequestPermittedToDelete(long requestId);
    boolean isNotificationBelongToPerson(Long notificationId, String personName);
    boolean isPersonIdEqualsToPrincipalPersonId(Long personId, Principal principal);
}

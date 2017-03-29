package com.netcracker.util.aop;

import com.netcracker.exception.CannotSendEmailException;
import com.netcracker.model.entity.Notification;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static com.netcracker.util.MessageConstant.SENDING_TO_DELETED_USER;

@Aspect
@Component
public class CatchDeletedUser {

    private final MessageSource messageSource;

    @Autowired
    public CatchDeletedUser(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Around("execution(void com.netcracker.service.mail.impls.MailService.send(..))")
    public void interceptMailSendingToDeletedUser(ProceedingJoinPoint joinPoint) throws Throwable {
        Locale locale = LocaleContextHolder.getLocale();
        Object[] args = joinPoint.getArgs();
        Notification notification  = (Notification) args[0];

        if (notification.getPerson().isDeleted()){
            throw new CannotSendEmailException(messageSource.getMessage(SENDING_TO_DELETED_USER,
                    new Object[]{notification.getPerson().getId() , notification.getPerson().getEmail()}, locale));
        }else{
            joinPoint.proceed();
        }
    }

}

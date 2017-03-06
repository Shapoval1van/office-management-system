package com.netcracker.service.mail.interfaces;

import com.netcracker.model.entity.Notification;

public interface MailSending {
    boolean send(String recipient, String subject, String message);
    boolean send(Notification notification);
}

package com.netcracker.service.mail.interfaces;

public interface MailSending {
    boolean send(String recipient, String subject, String message);
}

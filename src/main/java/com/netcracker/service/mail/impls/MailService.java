package com.netcracker.service.mail.impls;

import com.netcracker.model.entity.Notification;
import com.netcracker.service.mail.interfaces.MailSending;
import com.netcracker.util.NotificationTextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class MailService implements MailSending {
    @Value("${mail.login}")
    private String MAIL_LOGIN;

    @Inject
    private MailSender mailSender;
    @Autowired
    private NotificationTextBuilder notificationTextBuilder;

    /**
     * This method sends mail.
     * @param recipient Mail field 'To'
     * @param subject Mail Field 'Tittle'
     * @param message Message text
     * @return true in case message sent successful or false if exception occurred.
     */
    @Deprecated
    @Override
    public boolean send(String recipient, String subject, String message) {
        SimpleMailMessage msg = new SimpleMailMessage();
        try {
            msg.setFrom(MAIL_LOGIN);
            msg.setTo(recipient);
            msg.setSubject(subject);
            msg.setText(message);

            mailSender.send(msg);
        }catch (MailException e){
            // TODO add exception info to log
            return false;
        }

        return true;
    }

    @Override
    public void send(Notification notification) {
        SimpleMailMessage msg = new SimpleMailMessage();
        try {
            msg.setFrom(MAIL_LOGIN);
            msg.setTo(notification.getPerson().getEmail());
            msg.setSubject(notification.getSubject());
            msg.setText(notificationTextBuilder.buildText(notification));

            mailSender.send(msg);
        } catch (MailException e){
            // TODO add exception info to log
        }
    }
}

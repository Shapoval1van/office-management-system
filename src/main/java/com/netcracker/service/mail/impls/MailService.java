package com.netcracker.service.mail.impls;

import com.netcracker.exception.CannotSendEmailException;
import com.netcracker.model.entity.Notification;
import com.netcracker.model.event.NotificationSendingErrorEvent;
import com.netcracker.service.mail.interfaces.MailSending;
import com.netcracker.util.NotificationTextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.internet.MimeMessage;

@Service
public class MailService implements MailSending {
    @Value("${mail.login}")
    private String MAIL_LOGIN;

    @Inject
    private JavaMailSender mailSender;
    @Autowired
    private NotificationTextBuilder notificationTextBuilder;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

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
    @Async
    public void send(Notification notification) {
        try {
            MimeMessagePreparator preparator = new MimeMessagePreparator() {
                @Override
                public void prepare(MimeMessage mimeMessage) throws Exception {
                    MimeMessageHelper mimeHelper = new MimeMessageHelper(mimeMessage);
                    mimeHelper.setFrom(MAIL_LOGIN);
                    mimeHelper.setTo(notification.getPerson().getEmail());
                    mimeHelper.setSubject(notification.getSubject());
                    mimeHelper.setText(notificationTextBuilder.buildText(notification), true);
                }
            };

            mailSender.send(preparator);
        } catch (MailException e){
            eventPublisher.publishEvent(new NotificationSendingErrorEvent(notification));
            throw new CannotSendEmailException();
        }
    }
}

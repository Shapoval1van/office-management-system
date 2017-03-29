package com.netcracker.service.mail.impls;

import com.netcracker.exception.CannotSendEmailException;
import com.netcracker.model.entity.Notification;
import com.netcracker.model.event.NotificationSendingErrorEvent;
import com.netcracker.service.mail.interfaces.MailSending;
import com.netcracker.util.NotificationTextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

import java.util.Locale;

import static com.netcracker.util.MessageConstant.MAIL_SENDING_ERROR;

@Service
public class MailService implements MailSending {
    private final MessageSource messageSource;
    @Value("${mail.login}")
    private String MAIL_LOGIN;
    private JavaMailSender mailSender;
    private NotificationTextBuilder notificationTextBuilder;
    private ApplicationEventPublisher eventPublisher;


    @Autowired
    public MailService(JavaMailSender mailSender, NotificationTextBuilder textBuilder,
                       ApplicationEventPublisher eventPublisher, MessageSource messageSource) {
        this.mailSender = mailSender;
        this.notificationTextBuilder = textBuilder;
        this.eventPublisher = eventPublisher;
        this.messageSource = messageSource;
    }

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
        Locale locale = LocaleContextHolder.getLocale();
        try {
            MimeMessagePreparator preparator = (MimeMessage mimeMessage) -> {
                    MimeMessageHelper mimeHelper = new MimeMessageHelper(mimeMessage);
                    mimeHelper.setFrom(MAIL_LOGIN);
                    mimeHelper.setTo(notification.getPerson().getEmail());
                    mimeHelper.setSubject(notification.getSubject());
                    mimeHelper.setText(notificationTextBuilder.buildText(notification), true);
            };

            mailSender.send(preparator);
        } catch (MailException e){
            eventPublisher.publishEvent(new NotificationSendingErrorEvent(notification));
            throw new CannotSendEmailException(messageSource.getMessage(MAIL_SENDING_ERROR,
                    new Object[]{notification.getPerson().getId() , notification.getPerson().getEmail()}, locale));
        }
    }
}

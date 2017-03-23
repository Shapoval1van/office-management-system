package com.netcracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@PropertySource("classpath:mail.properties")
public class MailServiceConfig {

    @Value("${mail.login}")
    private String MAIL_LOGIN;
    @Value("${mail.password}")
    private String MAIL_PASSWORD;
    @Value("${mail.smtp.auth}")
    private String MAIL_SMTP_AUTH;
    @Value("${mail.smtp.strarttls.enable}")
    private String MAIL_STARTTLS_ENABLE;
    @Value("${mail.smtp.host}")
    private String MAIL_SMTP_HOST;
    @Value("${mail.smtp.port}")
    private String MAIL_SMTP_PORT;

    @Bean
    public JavaMailSender getMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(MAIL_SMTP_HOST);
        mailSender.setPort(Integer.valueOf(MAIL_SMTP_PORT));
        mailSender.setUsername(MAIL_LOGIN);
        mailSender.setPassword(MAIL_PASSWORD);
        mailSender.getJavaMailProperties().setProperty("mail.smtp.auth", MAIL_SMTP_AUTH);
        mailSender.getJavaMailProperties().setProperty("mail.smtp.starttls.enable", MAIL_STARTTLS_ENABLE);
        mailSender.setDefaultEncoding("UTF-8");

        return mailSender;
    }
}

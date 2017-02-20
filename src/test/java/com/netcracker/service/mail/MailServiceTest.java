package com.netcracker.service.mail;

import com.netcracker.service.mail.impls.MailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MailServiceTest {
    @Autowired
    MailService mailService;

    @Test
    public void testSuccessfulMailSending(){
        // mail data
        String recipient = "gr.yevhen@gmail.com";
        String subject = "TestMailMethod";
        String message = "Hello! This is testing message.";

        // assert true
        assertThat(mailService.send(recipient, subject, message), is(true));
    }
}

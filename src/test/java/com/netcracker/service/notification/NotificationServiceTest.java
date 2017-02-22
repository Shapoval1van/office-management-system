package com.netcracker.service.notification;

import com.netcracker.model.entity.Person;
import com.netcracker.service.notification.impls.NotificationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NotificationServiceTest {
    @Autowired
    NotificationService notificationService;
    Person person;

    @Before
    public void init(){
        person = new Person();
        person.setEmail("gr.yevhen@gmail.com");
        person.setFirstName("Yevhen");
    }

    @Test
    public void testSuccessSendPasswordReminderNotification(){
        assertThat(notificationService.sendPasswordReminder(person, "link"), is(true));
    }

    @Test
    public void testSuccessSendInformationNotification(){
        assertThat(notificationService.sendInformationNotification(person), is(true));
    }

    @Test
    public void testSuccessSendRegistrationNotification(){
        assertThat(notificationService.sendRegistrationCompletedNotification(person, "our_site"), is(true));
    }
}

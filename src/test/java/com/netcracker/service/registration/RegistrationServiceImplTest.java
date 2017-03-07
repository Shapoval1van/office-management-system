package com.netcracker.service.registration;

import com.netcracker.exception.BadEmployeeException;
import com.netcracker.exception.ResourceAlreadyExistsException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.model.entity.Token;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "classpath:/sql/test/registration-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RegistrationServiceImplTest {

    @Autowired
    private RegistrationService service;
    private final Integer ROLE_ID = 100;
    private final String TEST_TOKEN = "TEST_TOKEN";
    private final String NEW_EMPLOYEE_PASSWORD = "Password1";
    private final String ENABLED_EMPLOYEE_EMAIL = "enabled@mail.com";
    private final String DISABLED_EMPLOYEE_EMAIL = "a.p.syvenko@gmail.com";
    private final String REQUEST_LINK = "site.com";

    @Test
    public void mustAddNewNonEnabledPersonAndReturnToken() throws Exception {
        Person person = new Person();
        person.setFirstName("Anatolii");
        person.setLastName("Syvenko");
        person.setEmail("example@mail.com");
        person.setPassword(NEW_EMPLOYEE_PASSWORD);
        person.setRole(new Role(ROLE_ID));
        person.setEnabled(false);

        Token token = service.registerPerson(person, REQUEST_LINK ,ROLE_ID);

        assertNotNull(token);
        assertNotNull(token.getId());
        assertNotNull(token.getPerson());
        assertNotNull(token.getTokenValue());
        assertTrue(Calendar.getInstance().getTime().before(token.getDateExpired()));
    }

    @Test
    public void mustUpdateExpiredDateIfPersonDisabled() throws Exception {
        Person person = new Person();
        person.setFirstName("Anatolii");
        person.setLastName("Syvenko");
        person.setEmail(DISABLED_EMPLOYEE_EMAIL);
        person.setPassword(NEW_EMPLOYEE_PASSWORD);
        person.setRole(new Role(ROLE_ID));
        person.setEnabled(false);

        Token token = service.registerPerson(person, REQUEST_LINK, ROLE_ID);

        assertTrue(Calendar.getInstance().getTime().before(token.getDateExpired()));
    }

    @Test(expected = ResourceAlreadyExistsException.class)
    public void ifEmployeeAlreadyRegisteredAndEnabledThenThrowException() throws Exception {
        Person person = new Person();
        person.setEmail(ENABLED_EMPLOYEE_EMAIL);
        service.registerPerson(person, REQUEST_LINK, ROLE_ID);
    }

    @Test
    public void ifVerificationTokenNotExpiredThenPersonSetEnabled() throws BadEmployeeException, ResourceNotFoundException {
        Person person = service.confirmEmail(TEST_TOKEN);

        assertTrue(person.isEnabled());
    }
}
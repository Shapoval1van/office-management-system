package com.netcracker.service.registration;

import com.netcracker.exception.BadEmployeeException;
import com.netcracker.exception.ResourceAlreadyExistsException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.model.entity.VerificationToken;
import com.netcracker.repository.data.PersonRepository;
import com.netcracker.repository.data.RoleRepository;
import org.junit.After;
import org.junit.Before;
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
    RegistrationService service;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    RoleRepository roleRepository;
    final Integer ROLE_ID = 100;
    final String TEST_TOKEN = "TEST_TOKEN";
    final String NEW_EMPLOYEE_PASSWORD = "Password1";
    final String ENABLED_EMPLOYEE_EMAIL = "enabled@mail.com";
    final String DISABLED_EMPLOYEE_EMAIL = "a.p.syvenko@gmail.com";

    @Test
    public void mustAddNewNonEnabledPersonAndReturnToken() throws Exception {
        Person person = new Person();
        person.setFirstName("Anatolii");
        person.setLastName("Syvenko");
        person.setEmail("example@mail.com");
        person.setPassword(NEW_EMPLOYEE_PASSWORD);
        person.setRole(new Role(ROLE_ID));
        person.setEnabled(false);

        VerificationToken verificationToken = service.registerPerson(person);

        assertNotNull(verificationToken);
        assertNotNull(verificationToken.getId());
        assertNotNull(verificationToken.getPerson());
        assertNotNull(verificationToken.getToken());
        assertTrue(Calendar.getInstance().getTime().before(verificationToken.getDateExpired()));
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

        VerificationToken verificationToken = service.registerPerson(person);

        assertTrue(Calendar.getInstance().getTime().before(verificationToken.getDateExpired()));
    }

    @Test(expected = ResourceAlreadyExistsException.class)
    public void ifEmployeeAlreadyRegisteredAndEnabledThenThrowException() throws Exception {
        Person person = new Person();
        person.setEmail(ENABLED_EMPLOYEE_EMAIL);
        service.registerPerson(person);
    }

    @Test
    public void ifVerificationTokenNotExpiredThenPersonSetEnabled() throws BadEmployeeException, ResourceNotFoundException {
        Person person = service.confirmEmail(TEST_TOKEN);

        assertTrue(person.isEnabled());
    }
}
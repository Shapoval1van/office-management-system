package com.netcracker.service.registration;

import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.repository.data.PersonRepository;
import com.netcracker.repository.data.RoleRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RegistrationServiceImplTest {

    @Autowired
    RegistrationService service;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    RoleRepository roleRepository;
    Role employee;

    @Before
    public void setUp() throws Exception {
        employee = new Role();
        employee.setName(Role.ROLE_EMPLOYEE);
        employee = roleRepository.save(employee).get();
    }

    @After
    public void tearDown() throws Exception {
        personRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    public void mustAddNewNonEnabledPersonWithRoleEmployee() throws Exception {
        Person person = new Person();
        person.setFirstName("Anatolii");
        person.setLastName("Syvenko");
        person.setEmail("example@gmail.com");
        person.setPassword("Password1");
        person.setRole(employee);
        person.setEnabled(false);

        Person savedPerson = service.register(person);

        assertNotNull(savedPerson);
        assertNotNull(savedPerson.getId());
        assertNotNull(savedPerson.getRole());
        assertEquals(employee.getId(), savedPerson.getRole().getId());
        assertFalse(savedPerson.isEnabled());
    }


}
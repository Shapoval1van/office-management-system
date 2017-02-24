package com.netcracker.repository;

import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.repository.data.PersonRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Timestamp;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PersonRepositoryTest {
    @Autowired
    private PersonRepository personRepository;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private Person person;

    @Before
    public void init() {
        person = new Person();
        person.setId(2L);
        person.setFirstName("MANAGER-  2");
        person.setLastName("SMITH-  2");
        person.setEmail("test2@test.com");
        person.setPassword("test2");
        person.setRole(new Role(2));
        person.setEnabled(true);
    }

    @Test
    @Transactional
    @Rollback
    public void findOneTest() throws ResourceNotFoundException {
        Person person = personRepository.findOne(2L).orElseThrow(()
                -> new ResourceNotFoundException("No such person_id"));
        Assert.assertEquals(person, this.person);
    }

    @Test
    @Transactional
    @Rollback
    public void saveTest() throws ResourceNotFoundException {
        person.setId(null);
        person.setEmail("testEmail");
        person = personRepository.save(person).get();
        Person test = personRepository.findOne(person.getId()).get();
        Assert.assertEquals(test, person);
    }
}

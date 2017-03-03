package com.netcracker.repository;

import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.Priority;
import com.netcracker.repository.data.interfaces.PriorityRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@Sql(scripts = "classpath:/sql/test/repository-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest
public class PriorityRepositoryTest {
    @Autowired
    private PriorityRepository priorityRepository;

    private Priority priority;

    @Before
    public void init() {
        priority = new Priority();
        priority.setId(2);
        priority.setName("NORMAL");
    }

    @Test
    @Transactional
    @Rollback
    public void findOneTest() throws ResourceNotFoundException {
        Priority person = priorityRepository.findOne(2).orElseThrow(()
                -> new ResourceNotFoundException("No such priority_id"));
        Assert.assertEquals(person, this.priority);
    }

    @Test
    @Transactional
    @Rollback
    public void saveTest() throws ResourceNotFoundException {
        priority.setId(null);
        priority.setName("test");
        priority = priorityRepository.save(priority).get();
        Priority test = priorityRepository.findOne(priority.getId()).get();
        Assert.assertEquals(test, priority);
    }
}

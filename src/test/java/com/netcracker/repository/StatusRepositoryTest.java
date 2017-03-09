package com.netcracker.repository;

import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.Status;
import com.netcracker.repository.data.interfaces.StatusRepository;
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
public class StatusRepositoryTest {
    @Autowired
    private StatusRepository statusRepository;

    private Status status;

    @Before
    public void init() {
        status = new Status();
        status.setId(1);
        status.setName("FREE");
    }

    @Test
    @Transactional
    @Rollback
    public void findOneTest() throws ResourceNotFoundException {
        Status status = statusRepository.findOne(1).orElseThrow(()
                -> new ResourceNotFoundException("No such status_id"));
        Assert.assertEquals(status, this.status);
    }

    @Test
    @Transactional
    @Rollback
    public void saveTest() throws ResourceNotFoundException {
        status.setId(null);
        status.setName("Unique");
        status = statusRepository.save(status).get();
        Assert.assertEquals(statusRepository.findOne(status.getId()).get(), status);
    }
}

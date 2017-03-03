package com.netcracker.repository;

import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.*;
import com.netcracker.repository.data.interfaces.RequestRepository;
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

import java.sql.Timestamp;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@Sql(scripts = "classpath:/sql/test/repository-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest
public class RequestRepositoryTest {

    @Autowired
    private RequestRepository requestRepository;
    private Request request;

    @Before
    public void init() {
        Role role = new Role();
        role.setId(2);
        Person person = new Person();
        person.setId(2L);
        Status status = new Status();
        status.setId(2);
        Priority priority = new Priority();
        priority.setId(2);
        Request requestObject = new Request();
        requestObject.setId(1L);
        requestObject.setName("Request 1");
        requestObject.setDescription("I want 1 cup of coffee");
        requestObject.setPriority(priority);
        requestObject.setEmployee(person);
        requestObject.setCreationTime(Timestamp.valueOf("2017-02-24 00:59:02.184181"));
        requestObject.setStatus(status);
        request = requestObject;
    }

    @Test
    @Transactional
    @Rollback
    public void findOneTest() throws ResourceNotFoundException {
        Request request = requestRepository.findOne(1L).orElseThrow(()
                -> new ResourceNotFoundException("No such person_id"));
        Assert.assertEquals(request, this.request);
    }

    @Test
    @Transactional
    @Rollback
    public void saveTest() throws ResourceNotFoundException {
        request.setId(null);
        request = requestRepository.save(request).get();
        Request request2 = requestRepository.findOne(request.getId()).get();
        Assert.assertEquals(request, request2);
    }

    @Test
    @Transactional
    @Rollback
    public void  changeRequestStatusTest(){
        request.setId(null);
        Status status= new Status(5);
        request = requestRepository.findOne(3L).get();
        requestRepository.changeRequestStatus(request, status);
        request = requestRepository.findOne(3L).get();
        Assert.assertEquals(request.getStatus().getId(), new Integer(5));
    }

    @Test
    @Transactional
    @Rollback
    public void deleteRequestTest(){
        request.setId(null);
        request = requestRepository.findOne(4L).get();
        Assert.assertEquals(request.getName(), "Sub request");
        requestRepository.delete(request.getId());
        Assert.assertTrue(!requestRepository.findOne(4L).isPresent());
    }

    @Test
    @Transactional
    @Rollback
    public void updateRequestTest(){
        request.setId(null);
        request = requestRepository.findOne(3L).get();
        request.setName("Hello");
        request.setEstimate(Timestamp.valueOf("2017-02-29 00:59:02.184181"));
        Request updatedRequest = requestRepository.updateRequest(request).get();
        Assert.assertEquals(updatedRequest.getName(), "Hello");
        Assert.assertEquals(updatedRequest.getDescription(), "Request test description");

    }
}

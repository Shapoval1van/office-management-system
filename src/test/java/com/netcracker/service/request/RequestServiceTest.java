package com.netcracker.service.request;

import com.netcracker.exception.CannotCreateSubRequestException;
import com.netcracker.exception.CannotDeleteRequestException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.*;
import org.junit.Assert;
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
public class RequestServiceTest {

    @Autowired
    private RequestService requestService;
    private Request request;

    @Test
    @Transactional
    @Rollback
    public void saveSubRequestTest() throws CannotCreateSubRequestException {
        request = requestService.getRequestById(3L).get();

        Request subRequest = new Request();
        subRequest.setId(6L);
        subRequest.setName("Test Sub Request");
        subRequest.setDescription("Test Description of sub request");
        subRequest.setCreationTime(Timestamp.valueOf("2017-02-25 00:59:02.184181"));

        Request saveSubRequest = requestService.saveSubRequest(subRequest, request).get();

        Assert.assertEquals(saveSubRequest.getId(), new Long(6));
        Assert.assertEquals(saveSubRequest.getName(), "Test Sub Request");
        Assert.assertEquals(saveSubRequest.getStatus().getId(), new Integer(2));
        Assert.assertEquals(saveSubRequest.getParent(), request);
    }

    @Test(expected = CannotCreateSubRequestException.class)
    @Transactional
    @Rollback
    public void trySaveRequestToSubRequest() throws CannotCreateSubRequestException {
        request = requestService.getRequestById(4L).get();

        Request subRequest = new Request();
        subRequest.setId(6L);
        subRequest.setName("Test Sub Request");
        subRequest.setDescription("Test Description of sub request");
        subRequest.setCreationTime(Timestamp.valueOf("2017-02-25 00:59:02.184181"));

        requestService.saveSubRequest(subRequest, request).get();

    }

    @Test
    @Transactional
    @Rollback
    public void deleteSubRequestTest() throws CannotDeleteRequestException, ResourceNotFoundException {
        request = requestService.getRequestById(4L).get();
        requestService.deleteRequestById(request.getId());
        Assert.assertTrue(!requestService.getRequestById(4L).isPresent());
    }

    @Test
    @Transactional
    @Rollback
    public void deleteRequestByIdTest() throws CannotDeleteRequestException, ResourceNotFoundException {
        request = requestService.getRequestById(3L).get();
        requestService.deleteRequestById(request.getId());
        request = requestService.getRequestById(3L).get();
        Assert.assertEquals(request.getStatus().getId(), new Integer(5));
        Assert.assertTrue(requestService.getRequestById(3L).isPresent());
        Assert.assertTrue(!requestService.getRequestById(4L).isPresent());
    }

    @Test(expected = CannotDeleteRequestException.class)
    @Transactional
    @Rollback
    public void tryDeleteClosedRequest() throws CannotDeleteRequestException, ResourceNotFoundException {
        request = requestService.getRequestById(2L).get();
        requestService.deleteRequestById(request.getId());
    }
}

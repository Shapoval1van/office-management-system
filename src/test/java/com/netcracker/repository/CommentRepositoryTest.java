package com.netcracker.repository;

import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.Comment;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;
import com.netcracker.repository.data.interfaces.CommentRepository;
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
import java.util.List;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@Sql(scripts = "classpath:/sql/test/repository-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    private Comment comment;

    @Before
    public void init() {
        comment = new Comment();
        comment.setId(1L);
        comment.setBody("Body");
        comment.setPublishDate(Timestamp.valueOf("2017-01-01 00:00:00.000000"));
        comment.setRequest(new Request(1L));
        comment.setAuthor(new Person(2L));
    }

    @Test
    @Transactional
    @Rollback
    public void findOneTest() throws ResourceNotFoundException {
        Comment comment = commentRepository.findOne(1L).orElseThrow(()
                -> new ResourceNotFoundException("No comment with id " + this.comment.getId()));
        Assert.assertEquals(comment, this.comment);
    }

    @Test
    @Transactional
    @Rollback
    public void saveTest() {
        comment.setId(null);
        comment = commentRepository.save(comment).get();
        Comment comment = commentRepository.findOne(this.comment.getId()).get();
        Assert.assertEquals(comment, this.comment);

    }

    @Test
    @Transactional
    @Rollback
    public void getCommentsByRequestIdTest() {
        List<Comment> comments = commentRepository.findCommentByRequestId(1L);
        Comment comment = comments.get(0);
        Assert.assertEquals(comment, this.comment);
    }
}

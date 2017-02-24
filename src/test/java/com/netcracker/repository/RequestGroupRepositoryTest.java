package com.netcracker.repository;

import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.*;
import com.netcracker.repository.data.RequestGroupRepository;
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
public class RequestGroupRepositoryTest {
        @Autowired
        private RequestGroupRepository requestGroupRepository;

        private JdbcTemplate jdbcTemplate;

        @Autowired
        public void setDataSource(DataSource dataSource){
            this.jdbcTemplate = new JdbcTemplate(dataSource);
        }

        private RequestGroup requestGroup;

        @Before
        public void init() {
            requestGroup = new RequestGroup();
            requestGroup.setId(1);
            requestGroup.setName("Request group   1");
        }

        @Test
        @Transactional
        @Rollback
        public void findOneTest() throws ResourceNotFoundException {
            RequestGroup requestGroup = requestGroupRepository.findOne(1).orElseThrow(()
                    -> new ResourceNotFoundException("No such request_group_id"));
            Assert.assertEquals(requestGroup, this.requestGroup);
        }

        @Test
        @Transactional
        @Rollback
        public void saveTest() throws ResourceNotFoundException {
            requestGroup.setId(null);
            requestGroup = requestGroupRepository.save(requestGroup).get();
            Assert.assertEquals(requestGroupRepository.findOne(requestGroup.getId()).get(), requestGroup);
        }
}

package com.netcracker.repository;

import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.Role;
import com.netcracker.repository.data.interfaces.RoleRepository;
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
public class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    private Role role;

    @Before
    public void init() {
        role = new Role();
        role.setId(1);
        role.setName("ROLE_ADMINISTRATOR");
    }

    @Test
    @Transactional
    @Rollback
    public void findOneTest() throws ResourceNotFoundException {
        Role role = roleRepository.findOne(1).orElseThrow(()
                -> new ResourceNotFoundException("No such role_id"));
        Assert.assertEquals(role, this.role);
    }

    @Test
    @Transactional
    @Rollback
    public void saveTest() throws ResourceNotFoundException {
        role.setId(null);
        role.setName("TEST");
        role = roleRepository.save(role).get();
        Assert.assertEquals(roleRepository.findOne(role.getId()).get(), role);
    }
}

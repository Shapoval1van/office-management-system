package com.netcracker.repository;

import com.netcracker.model.entity.ChangeItem;
import com.netcracker.repository.data.interfaces.ChangeItemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@Sql(scripts = "classpath:/sql/test/repository-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest
public class ChangeItemRepositoryTest {

    @Autowired
    private ChangeItemRepository changeItemRepository;

    @Test
    public void test(){
        List<ChangeItem> changeItems= changeItemRepository.findAllByChangeGroupId((long)2);
        System.out.print("");
    }
}

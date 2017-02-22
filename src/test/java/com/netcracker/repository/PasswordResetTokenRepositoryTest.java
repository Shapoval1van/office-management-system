package com.netcracker.repository;


import com.netcracker.model.entity.PasswordResetToken;
import com.netcracker.model.entity.Person;
import com.netcracker.repository.data.PasswordResetTokenRepository;
import com.netcracker.repository.data.PersonRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:/sql/test/resetPassTest.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class PasswordResetTokenRepositoryTest {
    @Autowired
    private PasswordResetTokenRepository passResetTokenRepository;

    @Autowired
    private PersonRepository personRepository;

    @Test
    @Transactional()
    @Rollback
    public void writeReadToken(){
        Person person = new Person((long)1);
        String token = "testToken";
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, person);
        passResetTokenRepository.save(passwordResetToken);
        Optional<PasswordResetToken> actualPasswordResetToken = passResetTokenRepository.findTokenByPersonId((long)1);
        assertEquals(token ,actualPasswordResetToken.get().getToken());
        assertEquals(new Long(1), actualPasswordResetToken.get().getPerson().getId());
    }

}

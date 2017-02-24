package com.netcracker.service.passwordReset;

import com.netcracker.exception.OutdatedTokenException;
import com.netcracker.model.entity.PasswordResetToken;
import com.netcracker.model.entity.Person;
import com.netcracker.repository.data.PasswordResetTokenRepository;
import com.netcracker.repository.data.PersonRepository;
import com.netcracker.service.resetPassword.PasswordResetServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PasswordResetTest {
    private final String email = "test@test.com";
    private final String token = "token";
    private Person person;
    private PasswordResetToken passwordResetToken;

    @Mock
    PersonRepository personRepository;

    @Mock
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    PasswordResetServiceImpl passwordResetService;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        person = new Person();
        person.setId((long) 1);
        person.setPassword("test");
        person.setEmail(email);
        person.setEnabled(true);
        person.setLastName("test");
        person.setFirstName("test");

        passwordResetToken = new PasswordResetToken(token, person);

    }

    @Test
    public void resetPasswordValidEmailTest() {
        when(personRepository.findPersonByEmail(email)).thenReturn(Optional.ofNullable(this.person));
        when(passwordResetTokenRepository.findTokenByPersonId(person.getId())).thenReturn(Optional.empty());
        when(passwordResetTokenRepository.save(anyObject())).thenReturn(Optional.ofNullable(any()));
        PasswordResetToken passwordResetToken = passwordResetService.resetPassword(email);
        assertEquals(email, passwordResetToken.getPerson().getEmail());
        assertEquals(person.getLastName(), passwordResetToken.getPerson().getLastName());
        assertEquals(person.getFirstName(), passwordResetToken.getPerson().getFirstName());
        assertEquals(36, passwordResetToken.getToken().length());
    }

    @Test
    public void resetPasswordNotValidEmailTest() {
        when(personRepository.findPersonByEmail(email)).thenReturn(Optional.empty());
        PasswordResetToken passwordResetToken = passwordResetService.resetPassword(email);
        assertNull(passwordResetToken);
    }

    @Test
    public void updatePasswordForPersonWithValidToken() throws OutdatedTokenException {
        when(passwordResetTokenRepository.findOne(token)).thenReturn(Optional.ofNullable(passwordResetToken));
        when(personRepository.findOne(person.getId())).thenReturn(Optional.ofNullable(this.person));
        assertEquals(person , passwordResetService.updatePasswordForPersonByEmail("test", token));
    }

    @Test
    public void updatePasswordForPersonWithValidNotToken() throws OutdatedTokenException {
        when(passwordResetTokenRepository.findOne(token)).thenReturn(Optional.empty());
        assertNull(passwordResetService.updatePasswordForPersonByEmail("test", token));
    }

    @Test(expected = OutdatedTokenException.class)
    public void updatePasswordForPersonWithExpiredToken() throws OutdatedTokenException {
        passwordResetToken.setExpiryDate(new Date(System.currentTimeMillis()-10000 ));
        when(passwordResetTokenRepository.findOne(token)).thenReturn(Optional.ofNullable(passwordResetToken));
        passwordResetService.updatePasswordForPersonByEmail("test", token);
    }
}

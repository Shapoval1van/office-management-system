package com.netcracker.service.passwordReset;

import com.netcracker.exception.OutdatedTokenException;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Token;
import com.netcracker.model.entity.TokenType;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.TokenRepository;
import com.netcracker.service.resetPassword.PasswordResetServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
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
    private Token passwordResetToken;

    @Mock
    PersonRepository personRepository;

    @Mock
    TokenRepository passwordResetTokenRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Mock
    MessageSource messageSource;

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

        passwordResetToken = new Token(token, person, TokenType.RESET_PASSWORD);

    }

    @Test
    public void resetPasswordValidEmailTest() {
        when(personRepository.findPersonByEmail(email)).thenReturn(Optional.ofNullable(this.person));
        when(passwordResetTokenRepository.findResetPassTokenByPerson( person.getId())).thenReturn(Optional.empty());
        when(passwordResetTokenRepository.save(anyObject())).thenReturn(Optional.ofNullable(any()));
        Token passwordResetToken = passwordResetService.resetPassword(email, "someSite");
        assertEquals(email, passwordResetToken.getPerson().getEmail());
        assertEquals(person.getLastName(), passwordResetToken.getPerson().getLastName());
        assertEquals(person.getFirstName(), passwordResetToken.getPerson().getFirstName());
        assertEquals(36, passwordResetToken.getTokenValue().length());
    }

    @Test
    public void resetPasswordNotValidEmailTest() {
        when(personRepository.findPersonByEmail(email)).thenReturn(Optional.empty());
        Token passwordResetToken = passwordResetService.resetPassword(email, "someSite");
        assertNull(passwordResetToken);
    }

    @Test
    public void updatePasswordForPersonWithValidToken() throws OutdatedTokenException {
        when(passwordResetTokenRepository.findTokenByValue(token)).thenReturn(Optional.ofNullable(passwordResetToken));
        when(personRepository.findOne(person.getId())).thenReturn(Optional.ofNullable(this.person));
        assertEquals(person , passwordResetService.updatePasswordForPersonByEmail("test", token));
    }

    @Test
    public void updatePasswordForPersonWithValidNotToken() throws OutdatedTokenException {
        when(passwordResetTokenRepository.findTokenByValue(token)).thenReturn(Optional.empty());
        assertNull(passwordResetService.updatePasswordForPersonByEmail("test", token));
    }

    @Test(expected = OutdatedTokenException.class)
    public void updatePasswordForPersonWithExpiredToken() throws OutdatedTokenException {
        passwordResetToken.setDateExpired(new Date(System.currentTimeMillis()-10000 ));
        when(passwordResetTokenRepository.findTokenByValue(token)).thenReturn(Optional.ofNullable(passwordResetToken));
        passwordResetService.updatePasswordForPersonByEmail("test", token);
    }
}

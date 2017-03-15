package com.netcracker.service.resetPassword;


import com.netcracker.exception.OutdatedTokenException;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Token;
import com.netcracker.model.entity.TokenType;
import com.netcracker.model.event.ResetPasswordEvent;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static com.netcracker.util.MessageConstant.TOKEN_ERROR_EXPIRED;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApplicationEventPublisher eventPublisher;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Token resetPassword(String email, String siteLink) {
        Optional<Person> person = personRepository.findPersonByEmail(email);
        if (person.isPresent() && person.get().isEnabled()) {
            Optional<Token> oldResetPassToken = tokenRepository.findResetPassTokenByPerson(person.get().getId());
            String token = UUID.randomUUID().toString();
            oldResetPassToken.ifPresent((resetToken) -> tokenRepository.delete(resetToken.getId()));
            Token newPasswordResetToken = new Token(token, person.get(), TokenType.RESET_PASSWORD);
            tokenRepository.save(newPasswordResetToken);
            eventPublisher.publishEvent(new ResetPasswordEvent(siteLink, person.get(), newPasswordResetToken));
            return newPasswordResetToken;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Person updatePasswordForPersonByEmail(String password, String token) throws OutdatedTokenException {
        Locale locale = LocaleContextHolder.getLocale();
        Optional<Token> passwordResetToken = tokenRepository.findTokenByValue(token);
        if (passwordResetToken.isPresent()) {
            if (passwordResetToken.get().getDateExpired().getTime() < System.currentTimeMillis()) {
                throw new OutdatedTokenException(messageSource.getMessage(TOKEN_ERROR_EXPIRED, null, locale));
            }
            Optional<Person> person = personRepository.findOne(passwordResetToken.get().getPerson().getId());
            person.get().setPassword(passwordEncoder.encode(password));
            tokenRepository.delete(passwordResetToken.get().getId());
            personRepository.save(person.get());
            return person.get();
        }
        return null;
    }

}

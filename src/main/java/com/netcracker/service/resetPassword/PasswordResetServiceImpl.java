package com.netcracker.service.resetPassword;


import com.netcracker.exception.OutdatedTokenException;
import com.netcracker.model.entity.PasswordResetToken;
import com.netcracker.model.entity.Person;
import com.netcracker.repository.data.PasswordResetTokenRepository;
import com.netcracker.repository.data.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository resetTokenRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PasswordResetToken resetPassword(String email) {
        Optional<Person> person = personRepository.findPersonByEmail(email);
        if(person.isPresent() && person.get().isEnabled()){
            Optional<PasswordResetToken> oldResetPassToken = resetTokenRepository.findTokenByPersonId(person.get().getId());
            String token = UUID.randomUUID().toString();
            oldResetPassToken.ifPresent((resetToken)->resetTokenRepository.delete(resetToken.getId()));
            PasswordResetToken newPasswordResetToken = new PasswordResetToken(token, person.get());
            resetTokenRepository.save(newPasswordResetToken);
            return  newPasswordResetToken;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Person updatePasswordForPersonByEmail(String password, String token) throws OutdatedTokenException {
        Optional<PasswordResetToken> passwordResetToken = resetTokenRepository.findOne(token);
        if(passwordResetToken.isPresent()){
            if(passwordResetToken.get().getExpiryDate().getTime()<System.currentTimeMillis()){
                throw new OutdatedTokenException("Token was expired");
            }
            Optional<Person> person = personRepository.findOne(passwordResetToken.get().getPerson().getId());
            person.get().setPassword(passwordEncoder.encode(password));
            resetTokenRepository.delete(passwordResetToken.get().getId());
            personRepository.save(person.get());
            return  person.get();
        }
        return null;
    }

}

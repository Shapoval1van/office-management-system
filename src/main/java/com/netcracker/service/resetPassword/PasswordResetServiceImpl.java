package com.netcracker.service.resetPassword;


import com.netcracker.model.entity.PasswordResetToken;
import com.netcracker.model.entity.Person;
import com.netcracker.repository.data.PasswordResetTokenRepository;
import com.netcracker.repository.data.PersonRepository;
import com.netcracker.service.mail.interfaces.MailSending;
import org.springframework.beans.factory.annotation.Autowired;
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
    private MailSending mailSending;

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

}

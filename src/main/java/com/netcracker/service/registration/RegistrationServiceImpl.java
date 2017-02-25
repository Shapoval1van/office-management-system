package com.netcracker.service.registration;

import com.netcracker.exception.BadEmployeeException;
import com.netcracker.exception.ResourceAlreadyExistsException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.model.entity.VerificationToken;
import com.netcracker.model.event.PersonRegistrationEvent;
import com.netcracker.repository.data.PersonRepository;
import com.netcracker.repository.data.RoleRepository;
import com.netcracker.repository.data.VerificationTokenRepository;
import com.netcracker.service.notification.impls.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    static final int EXPIRATION = 60 * 24;

    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public VerificationToken registerPerson(Person person, String requestLink) throws Exception {

        Optional<Person> savedOptional = this.personRepository.findPersonByEmail(person.getEmail());

        if (savedOptional.isPresent()) {
            if (savedOptional.get().isEnabled()) {
                throw new ResourceAlreadyExistsException(Person.TABLE_NAME);
            } else {
                Optional<VerificationToken> oldTokenOptional = this.verificationTokenRepository.findVerificationTokenByPerson(savedOptional.get().getId());
                oldTokenOptional.ifPresent(verificationToken -> {
                    verificationToken.setDateExpired(this.calculateDateExpired());
                    this.publishOnRegistrationCompleteEvent(savedOptional.get() ,this.verificationTokenRepository.save(verificationToken), requestLink);
                });
                if (!oldTokenOptional.isPresent()) {
                    VerificationToken verificationToken = this.createVerificationToken(savedOptional.get());
                    Optional<VerificationToken> newTokenOptional = this.verificationTokenRepository.save(verificationToken);
                    this.publishOnRegistrationCompleteEvent(savedOptional.get() ,newTokenOptional, requestLink);
                    return newTokenOptional.orElse(null);
                }
                return oldTokenOptional.get();
            }
        }

        person.setEnabled(false);
        person.setRole(this.loadEmployeeRole());
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        Optional<Person> personOptional = this.personRepository.save(person);

        if (personOptional.isPresent()) {
            Optional<VerificationToken> verificationTokenOptional = this.verificationTokenRepository.save(this.createVerificationToken(person));
            this.publishOnRegistrationCompleteEvent(personOptional.get(), verificationTokenOptional, requestLink);
            return verificationTokenOptional.orElse(null);
        } else return null;
    }

    @Transactional
    @Override
    public Person confirmEmail(String token) throws BadEmployeeException, ResourceNotFoundException {

        VerificationToken verificationToken = this.verificationTokenRepository.findVerificationTokenByValueAndExpiredDate(token)
                .orElseThrow(() -> new BadEmployeeException(token));

        Person person = personRepository.findOne(verificationToken.getPerson().getId())
                .orElseThrow(() -> new ResourceNotFoundException(Person.TABLE_NAME));
        person.setEnabled(true);

        return personRepository.save(person).orElse(null);
    }


    private Role loadEmployeeRole() throws ResourceNotFoundException {
        return this.roleRepository.findRoleByName(Role.ROLE_EMPLOYEE)
                .orElseThrow(() -> new ResourceNotFoundException(Role.TABLE_NAME));
    }

    private VerificationToken createVerificationToken(Person person){
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(this.generateToken());
        verificationToken.setDateExpired(this.calculateDateExpired());
        verificationToken.setPerson(person);
        return verificationToken;
    }

    private Date calculateDateExpired() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, EXPIRATION);
        return new Date(cal.getTime().getTime());
    }

    private String generateToken(){
        return UUID.randomUUID().toString();
    }

    private void publishOnRegistrationCompleteEvent(Person person, Optional<VerificationToken> verificationToken, String requestLink){
        verificationToken.ifPresent((token) -> {
            PersonRegistrationEvent event = new PersonRegistrationEvent(requestLink, person, token);
            eventPublisher.publishEvent(event);
        });

    }
}

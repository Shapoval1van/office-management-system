package com.netcracker.service.registration;

import com.netcracker.exception.BadEmployeeException;
import com.netcracker.exception.ResourceAlreadyExistsException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.model.entity.VerificationToken;
import com.netcracker.repository.data.PersonRepository;
import com.netcracker.repository.data.RoleRepository;
import com.netcracker.repository.data.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    PersonRepository personRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    VerificationTokenRepository verificationTokenRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public VerificationToken registerPerson(Person person) throws Exception {

        Optional<Person> savedOptional = this.personRepository.findPersonByEmail(person.getEmail());

        if (savedOptional.isPresent()){
            if (savedOptional.get().isEnabled()){
                throw new ResourceAlreadyExistsException(Person.TABLE_NAME);
            } else {
                Optional<VerificationToken> vTokenOptional = this.verificationTokenRepository.findVerificationTokenByPerson(savedOptional.get().getId());
                vTokenOptional.ifPresent(verificationToken -> {
                            verificationToken.setDateExpired(this.calculateDateExpired());
                            this.verificationTokenRepository.save(verificationToken);
                });
                if (!vTokenOptional.isPresent()){
                    VerificationToken verificationToken = this.createVerificationToken(savedOptional.get());
                    return this.verificationTokenRepository.save(verificationToken).orElse(null);
                }
                return vTokenOptional.get();
            }
        }

        person.setEnabled(false);
        person.setRole(this.loadEmployeeRole());
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        Optional<Person> personOptional = this.personRepository.save(person);

        if (personOptional.isPresent()){
            VerificationToken verificationToken = this.createVerificationToken(person);
            return this.verificationTokenRepository.save(verificationToken).orElse(null);
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
}

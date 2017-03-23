package com.netcracker.service.person;

import com.netcracker.exception.CannotDeleteUserException;
import com.netcracker.exception.CannotUpdatePersonException;
import com.netcracker.model.dto.Page;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.model.event.DeleteUserEvent;
import com.netcracker.model.event.NotificationPersonUpdateEvent;
import com.netcracker.model.event.RecoverUserEvent;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.RequestRepository;
import com.netcracker.repository.data.interfaces.RoleRepository;
import com.netcracker.util.enums.role.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.netcracker.util.MessageConstant.*;

@Service
public class PersonServiceImpl implements PersonService {

    private  ApplicationEventPublisher eventPublisher;

    private final MessageSource messageSource;

    private final RoleRepository roleRepository;

    private final PersonRepository personRepository;

    private final RequestRepository requestRepository;

    @Autowired
    public PersonServiceImpl(MessageSource messageSource,
                             RoleRepository roleRepository,
                             PersonRepository personRepository,
                             ApplicationEventPublisher eventPublisher,
                             RequestRepository requestRepository) {
        this.messageSource = messageSource;
        this.roleRepository = roleRepository;
        this.personRepository = personRepository;
        this.eventPublisher = eventPublisher;
        this.requestRepository = requestRepository;
    }

    public Optional<Person> getPersonById(Long id) {
        Optional<Person> person = this.personRepository.findOne(id);
        person.ifPresent(this::fillPerson);
        return person;
    }



    @Override
    public Long getCountDeletedPersonByRole(Integer roleId) {
        return personRepository.getCountDeletedPersonByRole(roleId);
    }

    @Override
    public Optional<Person> deletePersonByEmail(String email, Principal principal) throws CannotDeleteUserException {
        Locale locale = LocaleContextHolder.getLocale();
        Person person = personRepository.findPersonByEmail(email).orElseThrow(() ->
                new CannotDeleteUserException(messageSource.getMessage(USER_WITH_EMAIL_NOT_PRESENT, null, locale)));

        if (RoleEnum.ADMINISTRATOR.getId().equals(person.getRole().getId())){
            if (requestRepository.countAllRequestByManager(person.getId()) != 0L){
                throw new CannotDeleteUserException(messageSource.getMessage(MANAGER_HAS_REQUESTS_ERROR, null, locale));
            }
            else if (principal.getName().equals(person.getEmail())){
                throw new CannotDeleteUserException(messageSource.getMessage(ADMINISTRATOR_REMOVING_ERROR, null, locale));
            }
            else{
                publishOnDeleteUserEvent(person);
                return Optional.of(disablePerson(person));
            }
        }
        else if (RoleEnum.PROJECT_MANAGER.getId().equals(person.getRole().getId())) {
            if (requestRepository.countAllRequestByManager(person.getId()) != 0L){
                throw new CannotDeleteUserException(messageSource.getMessage(MANAGER_HAS_REQUESTS_ERROR, null, locale));
            }
            else{
                publishOnDeleteUserEvent(person);
                return Optional.of(disablePerson(person));
            }
        }
        else {
            publishOnDeleteUserEvent(person);
            return Optional.of(disablePerson(person));
        }

    }


    private void publishOnDeleteUserEvent(Person person) {
        DeleteUserEvent event = new DeleteUserEvent(person);
        eventPublisher.publishEvent(event);
    }

    private void publishOnRecoverUserEvent(Person person) {
        RecoverUserEvent event = new RecoverUserEvent(person);
        eventPublisher.publishEvent(event);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR')")
    public Optional<Person> updatePerson(Person person, Long personId) throws CannotUpdatePersonException {
        Locale locale = LocaleContextHolder.getLocale();

        Optional<Person> oldUser = getPersonById(personId);
        if (!oldUser.isPresent()) return Optional.empty();
        if (RoleEnum.EMPLOYEE.getId().equals(oldUser.get().getRole().getId())){
            eventPublisher.publishEvent(new NotificationPersonUpdateEvent(person));
            this.personRepository.updatePerson(person);
            return Optional.of(person);
        } else if (RoleEnum.PROJECT_MANAGER.getId().equals(oldUser.get().getRole().getId())
                && RoleEnum.EMPLOYEE.getId().equals(person.getRole().getId()))
            throw new CannotUpdatePersonException(messageSource.getMessage(USER_ERROR_UPDATE_FROM_MANAGER_TO_EMPLOYEE, null, locale));
        else if (RoleEnum.ADMINISTRATOR.getId().equals(oldUser.get().getRole().getId())
                && RoleEnum.EMPLOYEE.getId().equals(person.getRole().getId()))
            throw new CannotUpdatePersonException(messageSource.getMessage(USER_ERROR_UPDATE_FROM_ADMIN_TO_EMPLOYEE, null, locale));
        else {
            eventPublisher.publishEvent(new NotificationPersonUpdateEvent(person));
            this.personRepository.updatePerson(person);
            return Optional.of(person);
        }
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR')")
    public List<Person> getManagers(Pageable pageable, String namePattern) {
        if(namePattern == null) {
            return this.personRepository.getManagers(pageable);
        }
        return this.personRepository.getManagers(pageable, namePattern);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR')")
    public List<Person> getUsersByNamePattern(Pageable pageable, String namePattern) {
        if(namePattern == null) {
            return this.personRepository.getPersonList(pageable);
        }
        return this.personRepository.getUsersByNamePattern(pageable, namePattern);

    }

    @Override
    public Optional<Person> findPersonByEmail(String email) {
        return this.personRepository.findPersonByEmail(email);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR')")
    public Page<Person> getPersonListByRole(Integer roleId, Pageable pageable) {
        Optional<Role> role = roleRepository.findOne(roleId);
        List<Person> personList = personRepository.getPersonListByRole(roleId, pageable, role);
        Long count = personRepository.getCountActivePersonByRole(roleId);

        personList.forEach(this::fillPerson);

        return new Page<>(pageable.getPageSize(), pageable.getPageNumber(), count, personList);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR')")
    public Page<Person> getPersonList(Pageable pageable) {
        List<Person> personList = personRepository.getPersonList(pageable);
        Long count = personRepository.getCountActivePerson();

        personList.forEach(this::fillPerson);

        return new Page<>(pageable.getPageSize(), pageable.getPageNumber(), count, personList);
    }

    @Override
    public List<Person> getDeletedPersonList(Integer roleId, Pageable pageable) {
        Optional<Role> role = roleRepository.findOne(roleId);
        List<Person> personList = personRepository.getDeletedPersons(roleId, pageable, role);
        personList.forEach(this::fillPerson);

        return personList;
    }

    @Override
    public Optional<Person> recoverDeletedPerson(String email) throws CannotUpdatePersonException {
        Locale locale = LocaleContextHolder.getLocale();
        Person person = personRepository.findPersonByEmail(email).orElseThrow(() ->
                new CannotUpdatePersonException(messageSource.getMessage(USER_WITH_EMAIL_NOT_PRESENT, null, locale)));
        person.setEnabled(true);
        person.setDeleted(false);
        publishOnRecoverUserEvent(person);
        personRepository.updatePersonAvailable(person);
        return Optional.of(person);
    }

    private Person disablePerson(Person person){
        person.setEnabled(false);
        person.setDeleted(true);
        personRepository.updatePersonAvailable(person);
        return person;
    }

    public void fillPerson(Person person){
        Role role = roleRepository.findRoleById(person.getRole().getId()).orElseGet(null);

        person.setRole(role);
    }

}

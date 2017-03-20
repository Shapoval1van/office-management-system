package com.netcracker.service.person;

import com.netcracker.exception.CannotUpdatePersonException;
import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.model.event.NotificationPersonUpdateEvent;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.RequestRepository;
import com.netcracker.repository.data.interfaces.RoleRepository;
import com.netcracker.util.enums.role.RoleEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.netcracker.util.MessageConstant.*;

@Service
public class PersonServiceImpl implements PersonService {

    private ApplicationEventPublisher eventPublisher;

    private final static Logger LOGGER = LoggerFactory.getLogger(PersonServiceImpl.class);

    private final MessageSource messageSource;

    private final RoleRepository roleRepository;

    private final PersonRepository personRepository;

    private final RequestRepository requestRepository;

    @Autowired
    public PersonServiceImpl(MessageSource messageSource,
                             RoleRepository roleRepository,
                             PersonRepository personRepository,
                             RequestRepository requestRepository,
                             ApplicationEventPublisher eventPublisher) {
        this.messageSource = messageSource;
        this.roleRepository = roleRepository;
        this.personRepository = personRepository;
        this.requestRepository = requestRepository;
        this.eventPublisher = eventPublisher;
    }

    public Optional<Person> getPersonById(Long id) {
        Optional<Person> person = this.personRepository.findOne(id);
        person.ifPresent(this::fillPerson);
        return person;
    }

    @Override
    public Long getCountActivePersonByRole(Integer roleId) {
        return personRepository.getCountActivePersonByRole(roleId);
    }


    @Override
    public Optional<Person> updatePerson(Person person, Long personId) throws CannotUpdatePersonException {
        Locale locale = LocaleContextHolder.getLocale();

        Optional<Person> oldUser = getPersonById(personId);
        if (!oldUser.isPresent()) return Optional.empty();
        if (RoleEnum.EMPLOYEE.getId().equals(oldUser.get().getRole().getId())) {
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
    public List<Person> getManagers(Pageable pageable, String namePattern) {
        if (namePattern == null) {
            return this.personRepository.getManagers(pageable);
        }
        return this.personRepository.getManagers(pageable, namePattern);
    }

    @Override
    public Optional<Person> findPersonByEmail(String email) {
        return this.personRepository.findPersonByEmail(email);
    }

    @Override
    public List<Person> getAvailablePersonList(Integer roleId, Pageable pageable) {
        Optional<Role> role = roleRepository.findOne(roleId);
        List<Person> personList = personRepository.getPersons(roleId, pageable, role);

        personList.forEach(this::fillPerson);

        return personList;
    }

    @Override
    public int subscribe(Long requestId, Principal principal) throws ResourceNotFoundException {
        Locale locale = LocaleContextHolder.getLocale();

        Person currentUser = getCurrentUser(principal);

        if (!requestRepository.findOne(requestId).isPresent()) {
            LOGGER.error(messageSource.getMessage(REQUEST_ERROR_NOT_EXIST, new Object[]{requestId}, locale));
            throw new ResourceNotFoundException(
                    messageSource.getMessage(REQUEST_ERROR_NOT_EXIST, new Object[]{requestId}, locale));
        }

        return personRepository.subscribe(requestId, currentUser.getId());
    }

    @Override
    public int unsubscribe(Long requestId, Principal principal) throws ResourceNotFoundException {
        Locale locale = LocaleContextHolder.getLocale();

        Person currentUser = getCurrentUser(principal);

        if (!requestRepository.findOne(requestId).isPresent()) {
            LOGGER.error(messageSource.getMessage(REQUEST_ERROR_NOT_EXIST, new Object[]{requestId}, locale));
            throw new ResourceNotFoundException(
                    messageSource.getMessage(REQUEST_ERROR_NOT_EXIST, new Object[]{requestId}, locale));
        }

        return personRepository.unsubscribe(requestId, currentUser.getId());
    }

    @Override
    public List<Person> getPersonsBySubscribingRequest(Long requestId) throws ResourceNotFoundException {
        Locale locale = LocaleContextHolder.getLocale();

        if (!requestRepository.findOne(requestId).isPresent()) {
            LOGGER.error(messageSource.getMessage(REQUEST_ERROR_NOT_EXIST, new Object[]{requestId}, locale));
            throw new ResourceNotFoundException(
                    messageSource.getMessage(REQUEST_ERROR_NOT_EXIST, new Object[]{requestId}, locale));
        }

        return personRepository.findPersonsBySubscribingRequest(requestId);
    }

    public void fillPerson(Person person) {
        Role role = roleRepository.findRoleById(person.getRole().getId()).orElseGet(null);

        person.setRole(role);
    }

    public Person getCurrentUser(Principal principal) throws CurrentUserNotPresentException {
        Locale locale = LocaleContextHolder.getLocale();

        if (principal == null) {
            LOGGER.error(messageSource.getMessage(USER_ERROR_NOT_PRESENT, null, locale));
            throw new CurrentUserNotPresentException(messageSource.getMessage(USER_ERROR_NOT_PRESENT, null, locale));
        }

        Optional<Person> personOptional = findPersonByEmail(principal.getName());

        if (!personOptional.isPresent()) {
            LOGGER.error(messageSource.getMessage(USER_ERROR_NOT_PRESENT, null, locale));
            throw new CurrentUserNotPresentException(messageSource.getMessage(USER_ERROR_NOT_PRESENT, null, locale));
        } else
            return personOptional.get();
    }

}

package com.netcracker.service.person;

import com.netcracker.exception.CannotDeleteUserException;
import com.netcracker.exception.CannotUpdatePersonException;
import com.netcracker.exception.CurrentUserNotPresentException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.dto.DeleteUserDTO;
import com.netcracker.model.dto.Page;
import com.netcracker.model.dto.PersonDTO;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;
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
import java.util.stream.Collectors;

import static com.netcracker.util.MessageConstant.*;

@Service
public class PersonServiceImpl implements PersonService {

    private ApplicationEventPublisher eventPublisher;

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

    public Optional<Person> getPersonById(Long id) throws CurrentUserNotPresentException {
        Optional<Person> person = checkPersonPresent(this.personRepository.findOne(id));
        person.ifPresent(this::fillPerson);
        return person;
    }

    //TODO wtf?
    @Override
    public Long getCountDeletedPersonByRole(Integer roleId) {
        return 1l;//personRepository.getCountDeletedPersonByRole(roleId);
    }

    @Override
    public Optional<DeleteUserDTO> deletePersonByEmail(String email, Principal principal) throws CannotDeleteUserException, CurrentUserNotPresentException {
        Locale locale = LocaleContextHolder.getLocale();
        Person person = checkPersonPresent(personRepository.findPersonByEmail(email)).get();
        if (RoleEnum.ADMINISTRATOR.getId().equals(person.getRole().getId())) {
            if (requestRepository.countAllRequestByManager(person.getId()) != 0L) {
                //throw new CannotDeleteUserException(messageSource.getMessage(MANAGER_HAS_REQUESTS_ERROR, null, locale));
                return Optional.of(new DeleteUserDTO(messageSource.getMessage(MANAGER_HAS_REQUESTS_ERROR, null, locale), false));
            } else if (principal.getName().equals(person.getEmail())) {
                return Optional.of(new DeleteUserDTO(messageSource.getMessage(ADMINISTRATOR_REMOVING_ERROR, null, locale), false));
            } else {
                publishOnDeleteUserEvent(person);
                disablePerson(person);
                return Optional.of(new DeleteUserDTO(messageSource.getMessage(USER_SUCCESFULLY_DELETED, null, locale), true));
            }
        } else if (RoleEnum.PROJECT_MANAGER.getId().equals(person.getRole().getId())) {
            if (requestRepository.countAllRequestByManager(person.getId()) != 0L) {
                //throw new CannotDeleteUserException(messageSource.getMessage(MANAGER_HAS_REQUESTS_ERROR, null, locale));
                return Optional.of(new DeleteUserDTO(messageSource.getMessage(MANAGER_HAS_REQUESTS_ERROR, null, locale), false));
            } else {
                publishOnDeleteUserEvent(person);
                disablePerson(person);
                return Optional.of(new DeleteUserDTO(messageSource.getMessage(USER_SUCCESFULLY_DELETED, null, locale), true));
            }
        } else {
            publishOnDeleteUserEvent(person);
            disablePerson(person);
            return Optional.of(new DeleteUserDTO(messageSource.getMessage(USER_SUCCESFULLY_DELETED, null, locale), true));
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

    /**
     * Update person
     * The person role cannot be updated from manager to employee
     * and from admin to employee, also admin cannot update himself
     *
     * @param person
     * @param personId
     * @return Optional<Person> updated person
     * @throws CannotUpdatePersonException
     */
    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR')")
    public Optional<Person> updatePerson(Person person, Long personId, Principal principal) throws CannotUpdatePersonException, CurrentUserNotPresentException {
        Locale locale = LocaleContextHolder.getLocale();

        Person currentAdmin = checkPersonPresent(personRepository.findPersonByEmail(principal.getName())).get();
        Optional<Person> oldUser = getPersonById(personId);
        if (!oldUser.isPresent()) return Optional.empty();

        if (currentAdmin.getId().equals(oldUser.get().getId())){
            throw new CannotUpdatePersonException(messageSource.getMessage(USER_ERROR_UPDATE_CURRENT_ADMIN, null, locale));
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
        if (namePattern == null)
            return this.personRepository.getManagers(pageable);
        return this.personRepository.getManagers(pageable, namePattern);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR')")
    public List<Person> getUsersByNamePattern(Pageable pageable, String namePattern) {
        if (namePattern == null)
            return this.personRepository.getPersonList(pageable);
        return this.personRepository.getUsersByNamePattern(pageable, namePattern);

    }

    @Override
    public Long getCountActivePersonByRole(Integer roleId) {
        return personRepository.getCountActivePersonByRole(roleId);
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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR')")
    public Page<Person> getPersonListByRole(Integer roleId, Pageable pageable) {
        Optional<Role> role = roleRepository.findOne(roleId);
        List<Person> personList = personRepository.getPersonListByRole(roleId, pageable, role);
        Long count = personRepository.getCountActivePersonByRole(roleId);
        personList.forEach(this::fillPerson);
        return new Page<>(pageable.getPageSize(), pageable.getPageNumber(), count, personList);
    }

    @Override
    public Page<Person> getDeletedPersonListByRole(Integer roleId, Pageable pageable) {
        Optional<Role> role = roleRepository.findOne(roleId);
        List<Person> personList = personRepository.getDeletedPersonListByRole(roleId, pageable, role);
        Long count = personRepository.getCountDeletedPersonByRole(roleId);
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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR')")
    public Page<Person> getDeletedPersonList(Pageable pageable) {
        List<Person> personList = personRepository.getDeletedPersonList(pageable);
        Long count = personRepository.getCountDeletedPerson();
        personList.forEach(this::fillPerson);
        return new Page<>(pageable.getPageSize(), pageable.getPageNumber(), count, personList);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR')")
    public Optional<Person> recoverDeletedPerson(String email) throws CurrentUserNotPresentException {
        Person person = checkPersonPresent(personRepository.findPersonByEmail(email)).get();
        person.setEnabled(true);
        person.setDeleted(false);
        publishOnRecoverUserEvent(person);
        personRepository.updatePersonAvailable(person);
        return Optional.of(person);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRATOR')")
    private Person disablePerson(Person person) {
        person.setEnabled(false);
        person.setDeleted(true);
        personRepository.updatePersonAvailable(person);
        return person;
    }

    @Override
    public int subscribe(Long requestId, Principal principal) throws ResourceNotFoundException {
        Person currentUser = getCurrentUser(principal);
        checkRequestPresent(requestRepository.findOne(requestId));
        return personRepository.subscribe(requestId, currentUser.getId());
    }

    @Override
    public int unsubscribe(Long requestId, Principal principal) throws ResourceNotFoundException {
        Person currentUser = getCurrentUser(principal);
        checkRequestPresent(requestRepository.findOne(requestId));
        return personRepository.unsubscribe(requestId, currentUser.getId());
    }

    @Override
    public List<PersonDTO> getPersonsBySubscribingRequest(Long requestId) throws ResourceNotFoundException {
        checkRequestPresent(requestRepository.findOne(requestId));
        return personRepository.findPersonsBySubscribingRequest(requestId)
                .stream()
                .map(PersonDTO::new)
                .collect(Collectors.toList());
    }

    public void fillPerson(Person person) {
        Role role = roleRepository.findRoleById(person.getRole().getId()).orElseGet(null);
        person.setRole(role);
    }

    public Person getCurrentUser(Principal principal) throws CurrentUserNotPresentException {
        Optional<Person> personOptional = checkPersonPresent(findPersonByEmail(principal.getName()));
        return personOptional.get();
    }

    private Optional<Person> checkPersonPresent(Optional<Person> person) throws CurrentUserNotPresentException{
        Locale locale = LocaleContextHolder.getLocale();
        if (!person.isPresent())
            throw new CurrentUserNotPresentException(messageSource.getMessage(USER_ERROR_NOT_PRESENT, null, locale));
        else
            return person;
    }

    private Optional<Request> checkRequestPresent(Optional<Request> request) throws ResourceNotFoundException{
        Locale locale = LocaleContextHolder.getLocale();
        if (!request.isPresent())
            throw new ResourceNotFoundException(messageSource.getMessage(REQUEST_ERROR_NOT_EXIST, null, locale));
        else
            return request;
    }

}

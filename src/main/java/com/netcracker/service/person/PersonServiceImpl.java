package com.netcracker.service.person;

import com.netcracker.exception.CannotUpdatePersonException;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.model.event.NotificationPersonUpdateEvent;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.impl.PersonRepositoryImpl;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.RoleRepository;
import com.netcracker.util.enums.role.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {

    private final ApplicationEventPublisher eventPublisher;

    private final RoleRepository roleRepository;

    private final PersonRepository personRepository;

    @Autowired
    public PersonServiceImpl(ApplicationEventPublisher eventPublisher,
                             RoleRepository roleRepository,
                             PersonRepository personRepository) {
        this.eventPublisher = eventPublisher;
        this.roleRepository = roleRepository;
        this.personRepository = personRepository;
    }

    public Optional<Person> getPersonById(Long id) {
        Optional<Person> person = this.personRepository.findOne(id);
        if(person.isPresent()) {
            Role role = this.roleRepository.findOne(person.get().getRole().getId()).orElseGet(null);
            person.get().setRole(role);
        }
        return person;
    }

    @Override
    public Optional<Person> updatePerson(Person person, Long personId) throws CannotUpdatePersonException {
        Optional<Person> oldUser = getPersonById(personId);
        if (!oldUser.isPresent()) return Optional.empty();
        if (RoleEnum.EMPLOYEE.getId().equals(oldUser.get().getRole().getId())){
            eventPublisher.publishEvent(new NotificationPersonUpdateEvent(person));
            this.personRepository.updatePerson(person);
            return Optional.of(person);
        } else if (RoleEnum.PROJECT_MANAGER.getId().equals(oldUser.get().getRole().getId())
                && RoleEnum.EMPLOYEE.getId().equals(person.getRole().getId()))
            throw new CannotUpdatePersonException("Cannot update user from manager to employee!");
        else if (RoleEnum.ADMINISTRATOR.getId().equals(oldUser.get().getRole().getId())
                && RoleEnum.EMPLOYEE.getId().equals(person.getRole().getId()))
            throw new CannotUpdatePersonException("Cannot update user from admin to employee!");
        else {
            eventPublisher.publishEvent(new NotificationPersonUpdateEvent(person));
            this.personRepository.updatePerson(person);
            return Optional.of(person);
        }
    }

    @Override
    public List<Person> getManagers(Pageable pageable, String namePattern) {
        if(namePattern == null) {
            return this.personRepository.getManagers(pageable);
        }
        return this.personRepository.getManagers(pageable, namePattern);
    }

    @Override
    public Optional<Person> findPersonByEmail(String email) {
        return this.personRepository.findPersonByEmail(email);
    }

//    @Override
//    public List<Person> getAdmins(Pageable pageable, Long currentAdminId) {
//        return this.personRepository.getAdmins(pageable, currentAdminId);
//    }

    @Override
    public List<Person> getAvailablePersonList(Integer roleId, Pageable pageable) {
        Optional<Role> role = roleRepository.findOne(roleId);
        List<Person> personList = role.isPresent() ? personRepository.queryForList(
                PersonRepositoryImpl.GET_AVAILABLE_PERSONS_BY_ROLE, pageable, roleId)
                : personRepository.queryForList(PersonRepositoryImpl.GET_AVAILABLE_PERSONS, pageable);

        return personList;
    }

}

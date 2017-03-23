package com.netcracker.service.person;

import com.netcracker.exception.CannotDeleteUserException;
import com.netcracker.exception.CannotUpdatePersonException;
import com.netcracker.model.dto.Page;
import com.netcracker.model.entity.Person;
import com.netcracker.repository.common.Pageable;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface PersonService {
    Optional<Person> getPersonById(Long id);


    Long getCountDeletedPersonByRole(Integer roleId);

    //Long getCountPassivePersons(Integer priorityId);

    Optional<Person> deletePersonByEmail(String email, Principal principal) throws CannotDeleteUserException;

    Optional<Person> updatePerson(Person person, Long personId) throws CannotUpdatePersonException;

    List<Person> getManagers(Pageable pageable, String namePattern);

    List<Person> getUsersByNamePattern(Pageable pageable, String namePattern);

    Optional<Person> findPersonByEmail(String email);


    Page<Person> getDeletedPersonList(Pageable pageable);

    Optional<Person> recoverDeletedPerson(String email) throws CannotUpdatePersonException;
    Page<Person> getPersonListByRole(Integer roleId, Pageable pageable);
    Page<Person> getDeletedPersonListByRole(Integer roleId, Pageable pageable);
    Page<Person> getPersonList(Pageable pageable);
}

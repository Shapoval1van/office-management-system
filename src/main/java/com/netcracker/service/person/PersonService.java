package com.netcracker.service.person;

import com.netcracker.exception.CannotUpdatePersonException;
import com.netcracker.model.dto.Page;
import com.netcracker.model.entity.Person;
import com.netcracker.repository.common.Pageable;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    Optional<Person> getPersonById(Long id);

    //Long getCountActivePersonByRole(Integer roleId);

    Optional<Person> updatePerson(Person person, Long personId) throws CannotUpdatePersonException;

    List<Person> getManagers(Pageable pageable, String namePattern);

    List<Person> getUsersByNamePattern(Pageable pageable, String namePattern);

    Optional<Person> findPersonByEmail(String email);

    Page<Person> getPersonListByRole(Integer roleId, Pageable pageable);

    Page<Person> getPersonList(Pageable pageable);
}

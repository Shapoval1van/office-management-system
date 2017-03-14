package com.netcracker.service.person;

import com.netcracker.exception.CannotUpdatePersonException;
import com.netcracker.model.entity.Person;
import com.netcracker.repository.common.Pageable;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    Optional<Person> getPersonById(Long id);

    Optional<Person> updatePerson(Person person, Long personId) throws CannotUpdatePersonException;

    List<Person> getManagers(Pageable pageable, String namePattern);

    Optional<Person> findPersonByEmail(String email);

    //List<Person> getAdmins(Pageable pageable, Long currentAdminId);

    List<Person> getAvailablePersonList(Integer roleId, Pageable pageable);
}

package com.netcracker.service.person;

import com.netcracker.exception.CannotUpdateUserException;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.repository.common.Pageable;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    Optional<Person> getPersonById(Long id);

    Optional<Person> updateUser(Person user, Long userId) throws CannotUpdateUserException;

    List<Person> getManagers(Pageable pageable, String namePattern);

    Optional<Person> findPersonByEmail(String email);

    List<Person> getAdmins(Pageable pageable, Long currentAdminId);
}

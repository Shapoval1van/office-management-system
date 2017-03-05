package com.netcracker.service.person;

import com.netcracker.model.entity.Person;
import com.netcracker.repository.common.Pageable;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    Optional<Person> getPersonById(Long id);

    List<Person> getManagers(Pageable pageable, String namePattern);
}

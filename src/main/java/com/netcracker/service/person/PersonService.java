package com.netcracker.service.person;

import com.netcracker.model.entity.Person;

import java.util.Optional;

public interface PersonService {
    Optional<Person> getPersonById(Long id);
}

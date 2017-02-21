package com.netcracker.repository.data;


import com.netcracker.model.entity.Person;
import com.netcracker.repository.common.JdbcRepository;

import java.util.Optional;

public interface PersonRepository extends JdbcRepository<Person, Long> {
    Optional<Person> findPersonByEmail(String email);
}

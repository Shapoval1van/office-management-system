package com.netcracker.repository.data;


import com.netcracker.model.entity.Person;
import com.netcracker.repository.common.JdbcRepository;

public interface PersonRepository extends JdbcRepository<Person, Long> {
}

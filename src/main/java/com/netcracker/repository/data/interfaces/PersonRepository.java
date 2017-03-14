package com.netcracker.repository.data.interfaces;


import com.netcracker.model.entity.Person;
import com.netcracker.repository.common.JdbcRepository;
import com.netcracker.repository.common.Pageable;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JdbcRepository<Person, Long> {
    Optional<Person> findPersonByEmail(String email);

    int updatePerson(Person person);

    List<Person> getManagers(Pageable pageable, String namePattern);

    List<Person> getManagers(Pageable pageable);

    //List<Person> getAdmins(Pageable pageable, Long currentAdminId);

}

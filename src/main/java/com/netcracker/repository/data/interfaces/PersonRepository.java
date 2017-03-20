package com.netcracker.repository.data.interfaces;


import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.repository.common.JdbcRepository;
import com.netcracker.repository.common.Pageable;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JdbcRepository<Person, Long> {
    Optional<Person> findPersonByEmail(String email);

    Long getCountActivePersonByRole(Integer roleId);

    int updatePerson(Person person);

    List<Person> getManagers(Pageable pageable, String namePattern);

    List<Person> getManagers(Pageable pageable);

    List<Person> getPersons(Integer roleId, Pageable pageable, Optional<Role> role);

    int subscribe(Long requestId, Long personId);

    int unsubscribe(Long requestId, Long personId);

    List<Person> findPersonsBySubscribingRequest(Long requestId);

}

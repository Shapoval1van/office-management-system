package com.netcracker.service.person;

import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PersonRepository personRepository;

    public Optional<Person> getPersonById(Long id) {
        Optional<Person> person = personRepository.findOne(id);
        if(person.isPresent()) {
            Role role = roleRepository.findOne(person.get().getRole().getId()).orElseGet(null);
            person.get().setRole(role);
        }

        return person;
    }
}

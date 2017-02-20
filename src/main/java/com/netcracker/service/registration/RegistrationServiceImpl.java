package com.netcracker.service.registration;

import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.repository.data.PersonRepository;
import com.netcracker.repository.data.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    PersonRepository personRepository;
    @Autowired
    RoleRepository roleRepository;

    @Transactional
    @Override
    public Person register(Person person) throws Exception {
        person.setEnabled(false);
        Optional<Role> roleOptional = roleRepository.findRoleByName(Role.ROLE_EMPLOYEE);
        person.setRole(roleOptional.orElseThrow(() -> new ResourceNotFoundException("Role")));
        return personRepository.save(person).orElse(null);
    }
}

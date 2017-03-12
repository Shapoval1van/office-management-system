package com.netcracker.service.person;

import com.netcracker.exception.CannotUpdateUserException;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Role;
import com.netcracker.repository.common.Pageable;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.RequestRepository;
import com.netcracker.repository.data.interfaces.RoleRepository;
import com.netcracker.util.enums.role.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RequestRepository requestRepository;

    public Optional<Person> getPersonById(Long id) {
        Optional<Person> person = this.personRepository.findOne(id);
        if(person.isPresent()) {
            Role role = this.roleRepository.findOne(person.get().getRole().getId()).orElseGet(null);
            person.get().setRole(role);
        }

        return person;
    }

    @Override
    public Optional<Person> updateUser(Person user, Long userId) throws CannotUpdateUserException {
        Optional<Person> oldUser = getPersonById(userId); //TODO
        if (!oldUser.isPresent()) return Optional.empty();
        if (RoleEnum.EMPLOYEE.getId().equals(oldUser.get().getRole().getId()))
            return this.personRepository.updateUser(user, userId);
        else {
            List<Request> requests = requestRepository.getAllAssignedRequest(oldUser.get().getId());
            if (!requests.isEmpty())
                throw new CannotUpdateUserException("User has assigned requests!");
            else return this.personRepository.updateUser(user, userId);
        }
    }


    @Override
    public List<Person> getManagers(Pageable pageable, String namePattern) {
        if(namePattern == null) {
            return this.personRepository.getManagers(pageable);
        }
        return this.personRepository.getManagers(pageable, namePattern);
    }

    @Override
    public Optional<Person> findPersonByEmail(String email) {
        return this.personRepository.findPersonByEmail(email);
    }

    @Override
    public List<Person> getAdmins(Pageable pageable, Long currentAdminId) {
        return this.personRepository.getAdmins(pageable, currentAdminId);
    }

}

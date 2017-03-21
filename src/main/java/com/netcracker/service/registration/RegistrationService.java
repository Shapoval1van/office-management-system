package com.netcracker.service.registration;


import com.netcracker.exception.BadEmployeeException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.model.entity.Token;

import java.util.List;

public interface RegistrationService {
    Token registerPerson(Person person, String requestLink, int id) throws Exception;
    List<Role> findAllRolesForAdminRegistration() throws Exception;
    Person confirmEmail(String token) throws BadEmployeeException, ResourceNotFoundException;
}

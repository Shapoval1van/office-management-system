package com.netcracker.service.registration;


import com.netcracker.exception.BadEmployeeException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.model.entity.Token;

import java.util.List;

public interface RegistrationService {
    // TODO: 07.03.2017 do not do this
    static final int EMPLOYEEID = 3;
    Token registerPerson(Person person, String requestLink, int id) throws Exception;
    List<Role> findAllRolesForAdminRegistration() throws Exception;
    Person confirmEmail(String token) throws BadEmployeeException, ResourceNotFoundException;
}

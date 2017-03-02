package com.netcracker.service.registration;


import com.netcracker.exception.BadEmployeeException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Token;

public interface RegistrationService {
    Token registerPerson(Person person, String requestLink, String role) throws Exception;

    Person confirmEmail(String token) throws BadEmployeeException, ResourceNotFoundException;
}

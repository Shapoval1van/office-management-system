package com.netcracker.service.registration;


import com.netcracker.exception.BadEmployeeException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.VerificationToken;

public interface RegistrationService {
    VerificationToken registerPerson(Person person) throws Exception;
    Person confirmEmail(String token) throws BadEmployeeException, ResourceNotFoundException;
}

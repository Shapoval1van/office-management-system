package com.netcracker.service.registration;


import com.netcracker.model.entity.Person;

import java.util.Optional;

public interface RegistrationService {
    Person register(Person person) throws Exception;
}

package com.netcracker.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.exception.BadEmployeeException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.dto.PersonDTO;
import com.netcracker.model.dto.RegistrationMessageDTO;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.model.entity.Token;
import com.netcracker.model.validation.CreateValidatorGroup;
import com.netcracker.model.view.View;
import com.netcracker.service.registration.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(RegistrationController.STATIC_MAPPING)
public class RegistrationController {

    public static final String STATIC_MAPPING = "api/v1/registration";
    public static final String JSON_MEDIA_TYPE = "application/json;";

    @Autowired
    private RegistrationService registrationService;

    @PostMapping(produces = JSON_MEDIA_TYPE)
    @ResponseStatus(HttpStatus.OK)
    public RegistrationMessageDTO registerEmployee(@Validated(CreateValidatorGroup.class) @RequestBody PersonDTO personDTO,
                                                   HttpServletRequest request) throws Exception {
        Person person = personDTO.toPerson();
        Token token = registrationService.registerPerson(person, this.buildRequestLink(request), Role.ROLE_EMPLOYEE);
        return new RegistrationMessageDTO(person.getEmail(), token.getDateExpired());
    }

    @PostMapping(value = "/create_administrator", produces = JSON_MEDIA_TYPE)
    @ResponseStatus(HttpStatus.OK)
    public RegistrationMessageDTO registerAdministrator(@Validated(CreateValidatorGroup.class) @RequestBody PersonDTO personDTO,
                                                        HttpServletRequest request) throws Exception {
        Person person = personDTO.toPerson();
        Token token = registrationService.registerPerson(person, this.buildRequestLink(request), Role.ROLE_ADMINISTRATOR);
        return new RegistrationMessageDTO(person.getEmail(), token.getDateExpired());
    }

    @PostMapping(value = "/create_office_manager", produces = JSON_MEDIA_TYPE)
    @ResponseStatus(HttpStatus.OK)
    public RegistrationMessageDTO registerOfficeManager(@Validated(CreateValidatorGroup.class) @RequestBody PersonDTO personDTO,
                                                        HttpServletRequest request) throws Exception {
        Person person = personDTO.toPerson();
        Token token = registrationService.registerPerson(person, this.buildRequestLink(request), Role.ROLE_OFFICE_MANAGER);
        return new RegistrationMessageDTO(person.getEmail(), token.getDateExpired());
    }

    @JsonView(View.Public.class)
    @GetMapping(value = "/{verificationToken}", produces = JSON_MEDIA_TYPE)
    @ResponseStatus(HttpStatus.OK)
    public PersonDTO confirmEmail(@PathVariable String verificationToken) throws BadEmployeeException, ResourceNotFoundException {
        Person person = registrationService.confirmEmail(verificationToken);
        return new PersonDTO(person);
    }

    private String buildRequestLink(HttpServletRequest request){
        return request.getScheme().concat("://").concat(request.getServerName()).concat(request.getContextPath());
    }

}
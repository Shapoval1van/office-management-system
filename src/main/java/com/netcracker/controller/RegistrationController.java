package com.netcracker.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.exception.BadEmployeeException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.dto.PersonDTO;
import com.netcracker.model.dto.RegistrationMessageDTO;
import com.netcracker.model.dto.RoleDTO;
import com.netcracker.model.dto.RolesDTO;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Role;
import com.netcracker.model.entity.Token;
import com.netcracker.model.validation.AdminCreateValidationGroup;
import com.netcracker.model.validation.CreateValidatorGroup;
import com.netcracker.model.view.View;
import com.netcracker.service.registration.RegistrationService;
import com.netcracker.util.enums.role.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(RegistrationController.STATIC_MAPPING)
public class RegistrationController {

    public static final String STATIC_MAPPING = "api/v1/registration";
    public static final String JSON_MEDIA_TYPE = "application/json;";
    private static final int TEMPORARY_PATHWORD_LENGTH = 20;

    @Autowired
    private RegistrationService registrationService;

    @PostMapping(produces = JSON_MEDIA_TYPE)
    @ResponseStatus(HttpStatus.OK)
    public RegistrationMessageDTO registerEmployee(@Validated(CreateValidatorGroup.class) @RequestBody PersonDTO personDTO,
                                                   HttpServletRequest request) throws Exception {
        Person person = personDTO.toPerson();
        Token token = registrationService.registerPerson(person, this.buildRequestLink(request), RoleEnum.EMPLOYEE.getId());
        return new RegistrationMessageDTO(person.getEmail(), token.getDateExpired());
    }

    @PostMapping(value = "/adminRegistrationPerson", produces = JSON_MEDIA_TYPE)
    @ResponseStatus(HttpStatus.OK)
    public RegistrationMessageDTO registerAdministrator(@Validated(AdminCreateValidationGroup.class) @RequestBody PersonDTO personDTO,
                                                        HttpServletRequest request) throws Exception {
        Person person = personDTO.toPerson();
        person.setPassword(buildRandomPassword());
        Token token = registrationService.registerPerson(person, this.buildRequestLink(request), person.getRole().getId());
        registrationService.confirmEmail(token.getTokenValue());
        return new RegistrationMessageDTO(person.getEmail(), token.getDateExpired());
    }

    @GetMapping(value="/roles", produces=JSON_MEDIA_TYPE)
    @ResponseStatus(HttpStatus.FOUND)
    public @ResponseBody ResponseEntity<RolesDTO> getAllRoles() throws Exception{
        List<Role> roleList = this.registrationService.findAllRolesForAdminRegistration();
        List<RoleDTO> roleDTOList = new ArrayList<>();
        for(Role role: roleList){
            roleDTOList.add(new RoleDTO(role));
        }
        RolesDTO rolesDTO = new RolesDTO(roleDTOList);
        return new ResponseEntity<RolesDTO>(rolesDTO, HttpStatus.ACCEPTED);
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

    private String buildRandomPassword(){
        String nums = "0123456789";
        String caps = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String let = "abcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(TEMPORARY_PATHWORD_LENGTH);
        sb.append(nums.charAt(rnd.nextInt(nums.length())));
        sb.append(caps.charAt(rnd.nextInt(caps.length())));
        for( int i = 0; i < TEMPORARY_PATHWORD_LENGTH - 2; i++ )
            sb.append(let.charAt(rnd.nextInt(let.length())));
        return sb.toString();
    }

}
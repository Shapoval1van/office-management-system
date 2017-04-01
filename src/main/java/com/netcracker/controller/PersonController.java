package com.netcracker.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.exception.CannotUpdatePersonException;
import com.netcracker.exception.IllegalAccessException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.dto.*;
import com.netcracker.model.entity.Person;
import com.netcracker.model.validation.CreateValidatorGroup;
import com.netcracker.model.validation.DeleteUserValidatorGroup;
import com.netcracker.model.view.View;
import com.netcracker.repository.common.Pageable;
import com.netcracker.service.person.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static com.netcracker.controller.RegistrationController.JSON_MEDIA_TYPE;

@RestController
@RequestMapping("/api/person")
public class PersonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    private PersonService personService;

    @GetMapping("/managers/{namePattern}")
    public ResponseEntity<?> getManagers(@PathVariable(required = false) String namePattern,
                                         Pageable pageable) {
        return ResponseEntity.ok(personService.getManagers(pageable, namePattern));
    }

    @GetMapping("/users/{namePattern}")
    public ResponseEntity<?> getUsersByNamePattern(@PathVariable(required = false) String namePattern,
                                         Pageable pageable) {
        return ResponseEntity.ok(personService.getUsersByNamePattern(pageable, namePattern));
    }

    @PostMapping(value = "/deletePerson", produces = JSON_MEDIA_TYPE)
    public DeleteUserDTO deletePerson(@Validated(DeleteUserValidatorGroup.class) @RequestBody String email,
                                      Principal principal) throws Exception {
        DeleteUserDTO messageDTO = personService.deletePersonByEmail(email, principal).get();
        return messageDTO;
    }

    @PostMapping(value = "/recoverPerson", produces = JSON_MEDIA_TYPE)
    public ResponseEntity<?> recoverPerson(@Validated(DeleteUserValidatorGroup.class) @RequestBody String email) throws Exception {
        return new ResponseEntity<>(personService.recoverDeletedPerson(email), HttpStatus.OK);
    }

    @PutMapping(produces = JSON_MEDIA_TYPE, value = "/{personId}")
    public ResponseEntity<Person> updatePerson(@PathVariable Long personId, Principal principal,
                                               @Validated(CreateValidatorGroup.class)
                                               @RequestBody PersonDTO personDTO) throws ResourceNotFoundException,
                                                IllegalAccessException, CannotUpdatePersonException {
        Person currentUser = personDTO.toPerson();
        currentUser.setId(personId);
        personService.updatePerson(currentUser, personId, principal);
        return new ResponseEntity<>(currentUser, HttpStatus.OK);
    }


    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/list/{roleId}")
    public ResponseEntity<?> getPersonListByRole(@PathVariable Integer roleId, Pageable pageable) {
        Page<Person> personPage = personService.getPersonListByRole(roleId, pageable);
        return ResponseEntity.ok(personPage);
    }

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/deleted-list/{roleId}")
    public ResponseEntity<?> getDeletedPersonListByRole(@PathVariable Integer roleId, Pageable pageable) {
        Page<Person> personPage = personService.getDeletedPersonListByRole(roleId, pageable);
        return ResponseEntity.ok(personPage);
    }

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/list")
    public ResponseEntity<?> getPersonList(Pageable pageable) {
        Page<Person> personPage = personService.getPersonList(pageable);
        return ResponseEntity.ok(personPage);
    }

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/deleted-list")
    public ResponseEntity<?> getDeletedPersonList(Pageable pageable) {
        Page<Person> personPage = personService.getDeletedPersonList(pageable);
        return ResponseEntity.ok(personPage);
    }



    @GetMapping("/{personId}")
    @JsonView(View.Public.class)
    @ResponseStatus(HttpStatus.OK)
    public FullPersonDTO getPersonById(@PathVariable("personId") Long id) throws ResourceNotFoundException {
        Optional<Person> personOptional = personService.getPersonById(id);
        return new FullPersonDTO(personOptional.get());
    }

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/count/{roleId}")
    public ResponseEntity<?> getCountActivePersonByRole(@PathVariable Integer roleId) {
        Long count = personService.getCountActivePersonByRole(roleId);
        return ResponseEntity.ok(count);

    }

    @PutMapping("/subscribe")
    @ResponseStatus(HttpStatus.OK)
    public int subscribe(@Validated(CreateValidatorGroup.class) @RequestBody SubscribeDTO subscribeDTO,
                         Principal principal) throws ResourceNotFoundException {
        return personService.subscribe(subscribeDTO.getRequestId(), principal);
    }

    @PutMapping("/unsubscribe")
    @ResponseStatus(HttpStatus.OK)
    public int unsubscribe(@Validated(CreateValidatorGroup.class) @RequestBody SubscribeDTO subscribeDTO,
                           Principal principal) throws ResourceNotFoundException {
        return personService.unsubscribe(subscribeDTO.getRequestId(), principal);
    }

    @GetMapping("/subscribers/request/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    @JsonView(View.Public.class)
    public List<PersonDTO> getPersonsBySubscribingRequest(@PathVariable("requestId") Long id) throws ResourceNotFoundException {
        return personService.getPersonsBySubscribingRequest(id);
    }
}

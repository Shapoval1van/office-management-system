package com.netcracker.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.dto.PersonDTO;
import com.netcracker.model.entity.Person;
import com.netcracker.model.view.View;
import com.netcracker.repository.common.Pageable;
import com.netcracker.service.person.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

    @GetMapping("/{personId}")
    @JsonView(View.Public.class)
    @ResponseStatus(HttpStatus.OK)
    public PersonDTO getPersonById(@PathVariable("personId") Long id) throws ResourceNotFoundException {
        Optional<Person> personOptional = personService.getPersonById(id);
        if (!personOptional.isPresent()) {
            LOGGER.error("Person with id {} not exist", id);
            throw new ResourceNotFoundException("Can't find person with id " + id);
        } else
            return new PersonDTO(personOptional.get());
    }
}

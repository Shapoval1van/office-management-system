package com.netcracker.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.exception.CannotUpdatePersonException;
import com.netcracker.exception.IllegalAccessException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.dto.MessageDTO;
import com.netcracker.model.dto.PersonDTO;
import com.netcracker.model.entity.Person;
import com.netcracker.model.validation.CreateValidatorGroup;
import com.netcracker.model.view.View;
import com.netcracker.repository.common.Pageable;
import com.netcracker.service.person.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.netcracker.controller.RegistrationController.JSON_MEDIA_TYPE;

@RestController
@RequestMapping("/api/person")
public class PersonController {


    @Autowired
    private PersonService personService;

    @GetMapping("/managers/{namePattern}")
    public ResponseEntity<?> getManagers(@PathVariable(required = false) String namePattern,
                                         Pageable pageable) {
        return ResponseEntity.ok(personService.getManagers(pageable, namePattern));
    }

    @JsonView(View.Public.class)
    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/{personId}")
    public ResponseEntity<?> getPerson(@PathVariable Long personId){
        Optional<Person> person = personService.getPersonById(personId);
        if (person.isPresent()) {
            return ResponseEntity.ok(new PersonDTO(person.get()));
        } else {
            return new ResponseEntity<>(new MessageDTO("No such id"), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(produces = JSON_MEDIA_TYPE, value = "/{personId}/update")
    public ResponseEntity<Person> updatePerson(@PathVariable Long personId,
                                               @Validated(CreateValidatorGroup.class) @RequestBody PersonDTO personDTO) throws ResourceNotFoundException, IllegalAccessException, CannotUpdatePersonException {
        Person currentUser = personDTO.toPerson();
        currentUser.setId(personId);
        Optional<Person> person = personService.updatePerson(currentUser, personId);
        if(!person.isPresent()){
            new ResponseEntity<>(currentUser, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(currentUser, HttpStatus.OK);
    }

//    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/admins")
//    public ResponseEntity<?> getListOfAdmins(Pageable pageable, Principal principal) {
//        Optional<Person> currentAdmin = personService.findPersonByEmail(principal.getName());
//        List<Person> admins = personService.getAdmins(pageable, currentAdmin.get().getId());
//        System.out.print(admins);
//        return ResponseEntity.ok((admins
//                .stream()
//                .map(PersonDTO::new)
//                .collect(Collectors.toList())));
//    }

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/persons/{roleId}")
    public ResponseEntity<?> getPersonList(@PathVariable Integer roleId, Pageable pageable) {
        List<Person> personList = personService.getAvailablePersonList(roleId, pageable);

        return ResponseEntity.ok((personList
                .stream()
                .map(PersonDTO::new)
                .collect(Collectors.toList())));
    }
}

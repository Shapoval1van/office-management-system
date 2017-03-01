package com.netcracker.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.exception.CannotCreateRequestException;
import com.netcracker.exception.CannotCreateSubRequestException;
import com.netcracker.exception.CannotDeleteRequestException;
import com.netcracker.model.dto.FullRequestDTO;
import com.netcracker.model.dto.RequestDTO;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Request;
import com.netcracker.model.validation.CreateValidatorGroup;
import com.netcracker.model.view.View;
import com.netcracker.repository.data.PersonRepository;
import com.netcracker.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/request")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    PersonRepository personRepository;

    private static final String JSON_MEDIA_TYPE = "application/json;";

    @JsonView(View.Public.class)
    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/{requestId}")
    public ResponseEntity<?> getRequest(@PathVariable Long requestId) {
        Optional<Request> request = requestService.getRequestById(requestId);
        if(request.isPresent()) {
            return ResponseEntity.ok(new FullRequestDTO(request.get()));
        } else {
            return new ResponseEntity<>("No such id", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(produces = JSON_MEDIA_TYPE, value = "/addRequest")
    public ResponseEntity<?> addRequest(@Validated(CreateValidatorGroup.class) @RequestBody RequestDTO requestDTO,
                                        Principal principal) {
        try {
            Request request = requestDTO.toRequest();
            Optional<Person> person = personRepository.findPersonByEmail(principal.getName());
            if(person.isPresent()) {
                request.setEmployee(person.get());
            } else {
                return new ResponseEntity<>("No such person", HttpStatus.BAD_REQUEST);
                // TODO log
            }
            requestService.saveRequest(request);
        } catch (CannotCreateRequestException e) {
            return new ResponseEntity<>(e.getDescription(), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("Added");
    }

    @PostMapping(produces = JSON_MEDIA_TYPE, value = "/addSubRequest")
    public ResponseEntity<?> addSubRequest(@Validated(CreateValidatorGroup.class) @RequestBody RequestDTO requestDTO,
                                           Principal principal) {
        try {
            Request subRequest = requestDTO.toRequest();
            Optional<Person> person = personRepository.findPersonByEmail(principal.getName());
            if(person.isPresent()) {
                subRequest.setManager(person.get());
            } else {
                return new ResponseEntity<>("No such person", HttpStatus.BAD_REQUEST);
                // TODO log
            }
            requestService.saveSubRequest(requestDTO.toRequest());
        } catch (CannotCreateSubRequestException e) {
            return new ResponseEntity<>(e.getDescription(), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("Added");
    }

    @PostMapping(produces = JSON_MEDIA_TYPE, value = "/updateRequest/{requestId}")
    public ResponseEntity<?> updateRequest(@Validated(CreateValidatorGroup.class) @PathVariable Long requestId) {
        try {
            Request request = requestService.getRequestById(requestId).get();
            requestService.updateRequest(request);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("Request updated");
    }

    @PostMapping(produces = JSON_MEDIA_TYPE, value = "/deleteRequest/{requestId}")
    public ResponseEntity<?> deleteRequest(@Validated(CreateValidatorGroup.class) @PathVariable Long requestId) {
        try {
            requestService.deleteRequestById(requestId);
        } catch (CannotDeleteRequestException e) {
            return new ResponseEntity<>(e.getDescription(), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("Request deleted");
    }
}
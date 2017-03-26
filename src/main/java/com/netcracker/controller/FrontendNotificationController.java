package com.netcracker.controller;


import com.netcracker.exception.CannotDeleteNotificationException;
import com.netcracker.model.dto.FrontendNotificationDTO;
import com.netcracker.service.frontendNotification.FrontendNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class FrontendNotificationController {


    @Autowired
    private FrontendNotificationService frontendNotificationService;


    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @RequestMapping("/api/notification/{personId}")
    public List<FrontendNotificationDTO> getAllNotificationToPerson(@PathVariable("personId") Long personId, Principal principal){
        return frontendNotificationService.getNotificationToPerson(personId, principal);
    }

    @DeleteMapping("/api/notification/delete")
    public ResponseEntity<?> deleteNotificationById(@RequestParam(name = "id",required=true) Long id, Principal principal) throws CannotDeleteNotificationException {
        frontendNotificationService.deleteNotificationById(id, principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/api/allNotification/delete")
    public ResponseEntity<?> deleteAllNotificationByPerson(@RequestParam(name = "id",required=true) Long id, Principal principal) throws CannotDeleteNotificationException {
        frontendNotificationService.deleteAllNotificationByPersonId(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

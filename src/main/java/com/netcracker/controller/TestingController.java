package com.netcracker.controller;

import com.netcracker.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestingController {

    @Autowired
    private RequestService requestService;

    @GetMapping("/notification/request/expiring")
    public ResponseEntity<?> notifyAboutExpiringEstimateTime(){
        requestService.checkRequestsForExpiry();
        return ResponseEntity.ok("Notified");
    }
}

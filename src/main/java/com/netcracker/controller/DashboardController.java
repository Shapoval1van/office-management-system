package com.netcracker.controller;

import com.netcracker.model.dto.Dashboard;
import com.netcracker.service.dashboard.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.security.Principal;

import static com.netcracker.controller.RegistrationController.JSON_MEDIA_TYPE;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/data")
    public ResponseEntity<?> getData(Principal principal) {
        Dashboard data = dashboardService.getData(principal);
        return ResponseEntity.ok(data);
    }

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/data/{userId}")
    public ResponseEntity<?> getDataByUser(@PathVariable Long userId) {
        Dashboard data = dashboardService.getDataByUser(userId);
        return ResponseEntity.ok(data);
    }
}

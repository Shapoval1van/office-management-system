package com.netcracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/secured")
public class SecuredController {

    @GetMapping
    public ResponseEntity securedMethod(){
        Map<String, String> map = new HashMap<>();
        map.put("message", "Hi, employee!");
        return ResponseEntity.ok(map);
    }
}

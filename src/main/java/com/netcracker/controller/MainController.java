package com.netcracker.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping(value = {"/*", "/login/*", "/resetPassword/*", "/request/*", "request/{requestId}/*", "/person/{personId}/*",
            "/request-group/{requestGroupId}/requests", "/report/*", "/test/**",
            "/admin/*", "/secured/**"}, produces = MediaType.TEXT_HTML_VALUE)
    public String goIndex() {
        return "/static/index.html";
    }

}

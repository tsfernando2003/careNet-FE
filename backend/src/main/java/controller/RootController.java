package com.CareNet.CareNet.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class RootController {

    @GetMapping("/")
    public String root() {
        return "Server is working successfully";
    }
}
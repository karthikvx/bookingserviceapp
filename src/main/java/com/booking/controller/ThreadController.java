package com.booking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ThreadController {

    @GetMapping("/thread-name")
    public String getThreadName() {
        return Thread.currentThread().toString();
    }
}
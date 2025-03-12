package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.model.Response;

@RestController
public class HelloController {
    
    @GetMapping("/")
    public Response hello() {
        return new Response("Hello, World!", "success");
    }
}
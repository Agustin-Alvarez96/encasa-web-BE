package com.encasa.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/secure")
    public String secure() {
        return "Access granted";
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hola, estás autenticado 🎉";
    }
}

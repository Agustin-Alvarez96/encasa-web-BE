package com.encasa.controllers;

import com.encasa.models.Service;
import com.encasa.repositories.ServiceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
public class ServiceController {

    private final ServiceRepository serviceRepository;

    public ServiceController(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @GetMapping
    public List<Service> getAll() {
        return serviceRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Service> getById(@PathVariable String id) {
        return serviceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

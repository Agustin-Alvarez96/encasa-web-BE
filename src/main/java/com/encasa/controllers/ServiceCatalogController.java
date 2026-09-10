package com.encasa.controllers;

import com.encasa.catalog.ServiceCatalog;
import com.encasa.dto.ServiceDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/services")
public class ServiceCatalogController {

    @GetMapping
    public List<ServiceDto> getAll() {
        return ServiceCatalog.ALL;
    }
}

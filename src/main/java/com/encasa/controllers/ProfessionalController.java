package com.encasa.controllers;

import com.encasa.dto.ProfessionalRequest;
import com.encasa.dto.ProfessionalResponse;
import com.encasa.services.ProfessionalService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/professionals")
public class ProfessionalController {

    private final ProfessionalService professionalService;

    public ProfessionalController(ProfessionalService professionalService) {
        this.professionalService = professionalService;
    }

    @GetMapping
    public List<ProfessionalResponse> getAll(@RequestParam(required = false) String serviceId,
                                              @RequestParam(required = false) String q) {
        return professionalService.getAll(serviceId, q);
    }

    @GetMapping("/{id}")
    public ProfessionalResponse getById(@PathVariable Long id) {
        return professionalService.getById(id);
    }

    @PostMapping("/me")
    public ProfessionalResponse upsertOwn(Authentication authentication, @RequestBody ProfessionalRequest request) {
        return professionalService.upsertOwn(authentication.getName(), request);
    }
}

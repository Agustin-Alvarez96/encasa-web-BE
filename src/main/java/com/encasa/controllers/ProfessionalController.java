package com.encasa.controllers;

import com.encasa.models.Professional;
import com.encasa.professionals.dto.ProfessionalRequest;
import com.encasa.repositories.ProfessionalRepository;
import com.encasa.services.ProfessionalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/professionals")
public class ProfessionalController {

    private final ProfessionalRepository professionalRepository;
    private final ProfessionalService professionalService;

    public ProfessionalController(ProfessionalRepository professionalRepository,
                                  ProfessionalService professionalService) {
        this.professionalRepository = professionalRepository;
        this.professionalService = professionalService;
    }

    @GetMapping
    public List<Professional> getAll(@RequestParam(required = false) String serviceId) {
        if (serviceId != null) {
            return professionalRepository.findByServiceId(serviceId);
        }
        return professionalRepository.findAll();
    }

    @GetMapping("/me")
    public ResponseEntity<Professional> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(professionalService.getMyProfile(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Professional> getById(@PathVariable Long id) {
        return professionalRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/me")
    public ResponseEntity<Professional> register(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProfessionalRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(professionalService.register(userDetails.getUsername(), request));
    }

    @PutMapping("/me")
    public ResponseEntity<Professional> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProfessionalRequest request) {
        return ResponseEntity.ok(professionalService.updateMyProfile(userDetails.getUsername(), request));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails userDetails) {
        professionalService.deleteMyProfile(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}

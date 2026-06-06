package com.encasa.professionals.dto;

public record ProfessionalRequest(
        String name,
        String serviceId,
        Integer hourlyRate,
        String image,
        String location,
        String description,
        Integer experience,
        String availability
) {}

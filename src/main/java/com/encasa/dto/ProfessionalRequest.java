package com.encasa.dto;

public record ProfessionalRequest(
        String name,
        String serviceId,
        Double hourlyRate,
        String image,
        String location,
        String description,
        Integer experience,
        String availability
) {}

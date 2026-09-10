package com.encasa.dto;

public record ProfessionalResponse(
        Long id,
        Long userId,
        String name,
        String service,
        String serviceId,
        double rating,
        int reviewCount,
        Double hourlyRate,
        String image,
        String location,
        String description,
        Integer experience,
        boolean verified,
        String availability
) {}

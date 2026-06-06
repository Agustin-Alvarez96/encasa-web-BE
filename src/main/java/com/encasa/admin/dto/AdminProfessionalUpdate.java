package com.encasa.admin.dto;

public record AdminProfessionalUpdate(
        String name,
        String serviceId,
        Integer hourlyRate,
        String image,
        String location,
        String description,
        Integer experience,
        String availability,
        Boolean verified
) {}

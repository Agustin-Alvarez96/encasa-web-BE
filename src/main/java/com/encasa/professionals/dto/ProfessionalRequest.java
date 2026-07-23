package com.encasa.professionals.dto;

import java.util.List;

public record ProfessionalRequest(
        String name,
        String serviceId,
        Integer hourlyRate,
        String image,
        String location,
        String description,
        String experience,
        String availability,
        List<String> tags
) {}

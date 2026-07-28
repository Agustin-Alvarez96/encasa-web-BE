package com.encasa.auth.dto;

public record UserProfileResponse(
        String id,
        String email,
        String name,
        String picture,
        String role,
        boolean hasProfessionalProfile,
        boolean emailNotifications
) {}

package com.encasa.users.dto;

public record UserProfileResponse(
        Long id,
        String email,
        String name,
        String phone,
        String avatar,
        String bio,
        String location,
        String role
) {}

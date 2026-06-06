package com.encasa.users.dto;

public record UpdateProfileRequest(
        String name,
        String phone,
        String avatar,
        String bio,
        String location
) {}

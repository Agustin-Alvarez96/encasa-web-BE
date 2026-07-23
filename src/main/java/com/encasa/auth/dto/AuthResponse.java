package com.encasa.auth.dto;

public record AuthResponse(
        String token,
        Long id,
        String email,
        String name,
        String role
) {}

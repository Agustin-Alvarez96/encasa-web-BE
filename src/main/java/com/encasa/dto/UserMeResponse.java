package com.encasa.dto;

public record UserMeResponse(
        Long id,
        String email,
        String role
) {}

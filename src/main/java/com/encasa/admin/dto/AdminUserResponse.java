package com.encasa.admin.dto;

public record AdminUserResponse(
        Long id,
        String email,
        String name,
        String phone,
        String location,
        String role
) {}

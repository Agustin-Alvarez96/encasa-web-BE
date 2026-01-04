package com.encasa.auth.dto;


public record LoginRequest(
        String email,
        String password
) {}


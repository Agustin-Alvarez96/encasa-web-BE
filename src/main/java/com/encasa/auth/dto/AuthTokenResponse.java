package com.encasa.auth.dto;

public record AuthTokenResponse(
        String token,
        UserProfileResponse user
) {}

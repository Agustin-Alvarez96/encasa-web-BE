package com.encasa.auth.dto;

public record OAuthGoogleRequest(
        String email,
        String name,
        String picture,
        String googleSub,
        String intent
) {}

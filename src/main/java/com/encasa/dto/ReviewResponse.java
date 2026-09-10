package com.encasa.dto;

public record ReviewResponse(
        Long id,
        Long bookingId,
        Long clientUserId,
        Long professionalId,
        Integer rating,
        String comment,
        String createdAt
) {}

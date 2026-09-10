package com.encasa.dto;

public record BookingResponse(
        Long id,
        Long clientUserId,
        String clientEmail,
        Long professionalId,
        String message,
        String status,
        String createdAt,
        String clientConfirmedAt,
        String professionalConfirmedAt
) {}

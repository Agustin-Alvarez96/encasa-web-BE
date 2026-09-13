package com.encasa.bookings.dto;

import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        Long clientUserId,
        String clientName,
        String clientEmail,
        Long professionalId,
        String serviceId,
        String serviceName,
        LocalDateTime scheduledDate,
        String status,
        String notes,
        Integer estimatedHours,
        Integer totalPrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime clientConfirmedAt,
        LocalDateTime professionalConfirmedAt
) {}

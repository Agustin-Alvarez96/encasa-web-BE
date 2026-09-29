package com.encasa.bookings.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ClientBookingResponse(
        Long id,
        Long professionalId,
        String professionalName,
        String professionalPhone,
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
        LocalDateTime professionalConfirmedAt,
        List<String> photoUrls
) {}

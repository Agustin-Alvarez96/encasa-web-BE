package com.encasa.bookings.dto;

import java.time.LocalDateTime;

public record CreateBookingRequest(
        Long professionalId,
        LocalDateTime scheduledDate,
        Integer estimatedHours,
        String notes
) {}

package com.encasa.bookings.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CreateBookingRequest(
        Long professionalId,
        LocalDateTime scheduledDate,
        Integer estimatedHours,
        String notes,
        List<String> photoUrls
) {}

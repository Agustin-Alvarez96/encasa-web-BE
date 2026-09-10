package com.encasa.dto;

public record ReviewRequest(
        Long bookingId,
        Integer rating,
        String comment
) {}

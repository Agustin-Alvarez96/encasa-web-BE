package com.encasa.reviews.dto;

public record CreateReviewRequest(
        Long bookingId,
        Integer rating,
        String comment
) {}

package com.encasa.controllers;

import com.encasa.models.Review;
import com.encasa.reviews.dto.CreateReviewRequest;
import com.encasa.services.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<Review> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.create(userDetails.getUsername(), request));
    }

    @GetMapping
    public List<Review> getAll() {
        return reviewService.getAll();
    }

    @GetMapping("/professional/{professionalId}")
    public List<Review> getByProfessional(@PathVariable Long professionalId) {
        return reviewService.getByProfessional(professionalId);
    }

    @GetMapping("/me")
    public List<Review> getMyReviews(@AuthenticationPrincipal UserDetails userDetails) {
        return reviewService.getMyReviews(userDetails.getUsername());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        reviewService.delete(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}

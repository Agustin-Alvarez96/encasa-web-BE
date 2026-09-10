package com.encasa.controllers;

import com.encasa.dto.ReviewRequest;
import com.encasa.dto.ReviewResponse;
import com.encasa.services.ReviewService;
import org.springframework.security.core.Authentication;
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
    public ReviewResponse create(Authentication authentication, @RequestBody ReviewRequest request) {
        return reviewService.create(authentication.getName(), request);
    }

    @GetMapping
    public List<ReviewResponse> getAll() {
        return reviewService.listAll();
    }

    @GetMapping("/professional/{id}")
    public List<ReviewResponse> getForProfessional(@PathVariable Long id) {
        return reviewService.listForProfessional(id);
    }
}

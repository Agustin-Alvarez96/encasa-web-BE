package com.encasa.controllers;

import com.encasa.dto.BookingRequest;
import com.encasa.dto.BookingResponse;
import com.encasa.services.BookingService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingResponse create(Authentication authentication, @RequestBody BookingRequest request) {
        return bookingService.create(authentication.getName(), request);
    }

    @GetMapping("/mine")
    public List<BookingResponse> getMine(Authentication authentication) {
        return bookingService.getMine(authentication.getName());
    }

    @PatchMapping("/{id}/status")
    public BookingResponse markInProgress(Authentication authentication, @PathVariable Long id) {
        return bookingService.markInProgress(authentication.getName(), id);
    }

    @PostMapping("/{id}/confirm-completion")
    public BookingResponse confirmCompletion(Authentication authentication, @PathVariable Long id) {
        return bookingService.confirmCompletion(authentication.getName(), id);
    }
}

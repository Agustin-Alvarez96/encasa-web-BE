package com.encasa.controllers;

import com.encasa.bookings.dto.CreateBookingRequest;
import com.encasa.models.Booking;
import com.encasa.services.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<Booking> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.create(userDetails.getUsername(), request));
    }

    @GetMapping("/me")
    public List<Booking> myBookingsAsClient(
            @AuthenticationPrincipal UserDetails userDetails) {
        return bookingService.getMyBookingsAsClient(userDetails.getUsername());
    }

    @GetMapping("/professional")
    public List<Booking> myBookingsAsProfessional(
            @AuthenticationPrincipal UserDetails userDetails) {
        return bookingService.getMyBookingsAsProfessional(userDetails.getUsername());
    }

    @PutMapping("/{id}/confirm")
    public Booking confirm(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return bookingService.confirm(userDetails.getUsername(), id);
    }

    @PutMapping("/{id}/cancel")
    public Booking cancel(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return bookingService.cancel(userDetails.getUsername(), id);
    }

    @PutMapping("/{id}/complete")
    public Booking complete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return bookingService.complete(userDetails.getUsername(), id);
    }
}

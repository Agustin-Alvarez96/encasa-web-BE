package com.encasa.services;

import com.encasa.dto.ReviewRequest;
import com.encasa.dto.ReviewResponse;
import com.encasa.models.Booking;
import com.encasa.models.BookingStatus;
import com.encasa.models.Professional;
import com.encasa.models.Review;
import com.encasa.models.User;
import com.encasa.repositories.ProfessionalRepository;
import com.encasa.repositories.ReviewRepository;
import com.encasa.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProfessionalRepository professionalRepository;
    private final UserRepository userRepository;
    private final BookingService bookingService;

    public ReviewService(ReviewRepository reviewRepository,
                          ProfessionalRepository professionalRepository,
                          UserRepository userRepository,
                          BookingService bookingService) {
        this.reviewRepository = reviewRepository;
        this.professionalRepository = professionalRepository;
        this.userRepository = userRepository;
        this.bookingService = bookingService;
    }

    public ReviewResponse create(String clientEmail, ReviewRequest request) {
        if (request.bookingId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta bookingId");
        }
        if (request.rating() == null || request.rating() < 1 || request.rating() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rating debe estar entre 1 y 5");
        }

        User client = userRepository.findByEmail(clientEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        Booking booking = bookingService.resolveAutoComplete(bookingService.findEntity(request.bookingId()));

        if (!booking.getClientUserId().equals(client.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta solicitud no te pertenece");
        }
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La solicitud todavía no fue confirmada como completada por ambas partes");
        }
        if (reviewRepository.existsByBookingId(booking.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya calificaste esta solicitud");
        }

        Professional professional = professionalRepository.findById(booking.getProfessionalId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesional no encontrado"));
        if (professional.getUserId().equals(client.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No podés calificarte a vos mismo");
        }

        Review review = new Review();
        review.setBookingId(booking.getId());
        review.setClientUserId(client.getId());
        review.setProfessionalId(professional.getId());
        review.setRating(request.rating());
        review.setComment(request.comment());

        return toResponse(reviewRepository.save(review));
    }

    public List<ReviewResponse> listAll() {
        return reviewRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<ReviewResponse> listForProfessional(Long professionalId) {
        return reviewRepository.findByProfessionalId(professionalId).stream().map(this::toResponse).toList();
    }

    private ReviewResponse toResponse(Review r) {
        return new ReviewResponse(
                r.getId(),
                r.getBookingId(),
                r.getClientUserId(),
                r.getProfessionalId(),
                r.getRating(),
                r.getComment(),
                r.getCreatedAt().toString()
        );
    }
}

package com.encasa.services;

import com.encasa.models.Booking;
import com.encasa.models.Professional;
import com.encasa.models.Review;
import com.encasa.models.User;
import com.encasa.repositories.BookingRepository;
import com.encasa.repositories.ProfessionalRepository;
import com.encasa.repositories.ReviewRepository;
import com.encasa.repositories.UserRepository;
import com.encasa.reviews.dto.CreateReviewRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final ProfessionalRepository professionalRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         BookingRepository bookingRepository,
                         ProfessionalRepository professionalRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
        this.professionalRepository = professionalRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Review create(String clientEmail, CreateReviewRequest req) {
        if (req.rating() == null || req.rating() < 1 || req.rating() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rating debe estar entre 1 y 5");
        }

        User client = findUser(clientEmail);

        Booking booking = bookingRepository.findById(req.bookingId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        if (!booking.getClientUserId().equals(client.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo podés reseñar tus propias reservas");
        }

        if (booking.getStatus() != Booking.Status.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La reserva debe estar completada para dejar una reseña");
        }

        if (reviewRepository.findByBookingId(req.bookingId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta reserva ya tiene una reseña");
        }

        Review review = new Review();
        review.setBookingId(req.bookingId());
        review.setClientUserId(client.getId());
        review.setProfessionalId(booking.getProfessionalId());
        review.setRating(req.rating());
        review.setComment(req.comment());

        Review saved = reviewRepository.save(review);
        recalculateProfessionalRating(booking.getProfessionalId());
        return saved;
    }

    public List<Review> getByProfessional(Long professionalId) {
        return reviewRepository.findByProfessionalIdOrderByCreatedAtDesc(professionalId);
    }

    public List<Review> getMyReviews(String email) {
        Long userId = findUser(email).getId();
        return reviewRepository.findByClientUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void delete(String email, Long reviewId) {
        User client = findUser(email);
        Review review = findReview(reviewId);
        if (!review.getClientUserId().equals(client.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo podés eliminar tus propias reseñas");
        }
        Long professionalId = review.getProfessionalId();
        reviewRepository.delete(review);
        recalculateProfessionalRating(professionalId);
    }

    public List<Review> getAll() {
        return reviewRepository.findAll();
    }

    @Transactional
    public void adminDelete(Long reviewId) {
        Review review = findReview(reviewId);
        Long professionalId = review.getProfessionalId();
        reviewRepository.delete(review);
        recalculateProfessionalRating(professionalId);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void recalculateProfessionalRating(Long professionalId) {
        Professional professional = professionalRepository.findById(professionalId).orElse(null);
        if (professional == null) return;

        List<Review> reviews = reviewRepository.findByProfessionalId(professionalId);
        if (reviews.isEmpty()) {
            professional.setRating(0.0);
            professional.setReviewCount(0);
        } else {
            double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
            professional.setRating(Math.round(avg * 10.0) / 10.0);
            professional.setReviewCount(reviews.size());
        }
        professionalRepository.save(professional);
    }

    private Review findReview(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reseña no encontrada"));
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
}

package com.encasa.repositories;

import com.encasa.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProfessionalId(Long professionalId);

    boolean existsByBookingId(Long bookingId);

    Optional<Review> findByBookingId(Long bookingId);
}

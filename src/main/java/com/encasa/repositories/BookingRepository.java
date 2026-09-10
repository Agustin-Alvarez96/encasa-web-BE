package com.encasa.repositories;

import com.encasa.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByClientUserId(Long clientUserId);

    List<Booking> findByProfessionalId(Long professionalId);
}

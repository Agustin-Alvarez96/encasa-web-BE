package com.encasa.repositories;

import com.encasa.models.Professional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfessionalRepository extends JpaRepository<Professional, Long> {
    List<Professional> findByServiceId(String serviceId);
    Optional<Professional> findByUserId(Long userId);
}

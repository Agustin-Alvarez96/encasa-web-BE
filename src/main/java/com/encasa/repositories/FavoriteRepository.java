package com.encasa.repositories;

import com.encasa.models.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    Optional<Favorite> findByUserIdAndProfessionalId(Long userId, Long professionalId);
    void deleteByUserIdAndProfessionalId(Long userId, Long professionalId);
    boolean existsByUserIdAndProfessionalId(Long userId, Long professionalId);
}

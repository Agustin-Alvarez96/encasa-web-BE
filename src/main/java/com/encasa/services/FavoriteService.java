package com.encasa.services;

import com.encasa.models.Favorite;
import com.encasa.models.Professional;
import com.encasa.models.User;
import com.encasa.repositories.FavoriteRepository;
import com.encasa.repositories.ProfessionalRepository;
import com.encasa.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProfessionalRepository professionalRepository;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           UserRepository userRepository,
                           ProfessionalRepository professionalRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.professionalRepository = professionalRepository;
    }

    public List<Professional> getFavorites(String email) {
        Long userId = getUser(email).getId();
        return favoriteRepository.findByUserId(userId).stream()
                .map(f -> professionalRepository.findById(f.getProfessionalId()).orElse(null))
                .filter(p -> p != null)
                .toList();
    }

    public void addFavorite(String email, Long professionalId) {
        Long userId = getUser(email).getId();
        if (!professionalRepository.existsById(professionalId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesional no encontrado");
        }
        if (!favoriteRepository.existsByUserIdAndProfessionalId(userId, professionalId)) {
            favoriteRepository.save(new Favorite(userId, professionalId));
        }
    }

    @Transactional
    public void removeFavorite(String email, Long professionalId) {
        Long userId = getUser(email).getId();
        favoriteRepository.deleteByUserIdAndProfessionalId(userId, professionalId);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
}

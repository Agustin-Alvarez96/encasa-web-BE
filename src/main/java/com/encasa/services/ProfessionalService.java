package com.encasa.services;

import com.encasa.catalog.ServiceCatalog;
import com.encasa.dto.ProfessionalRequest;
import com.encasa.dto.ProfessionalResponse;
import com.encasa.models.Professional;
import com.encasa.models.Review;
import com.encasa.models.Role;
import com.encasa.models.User;
import com.encasa.repositories.ProfessionalRepository;
import com.encasa.repositories.ReviewRepository;
import com.encasa.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ProfessionalService(ProfessionalRepository professionalRepository,
                                ReviewRepository reviewRepository,
                                UserRepository userRepository) {
        this.professionalRepository = professionalRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    public List<ProfessionalResponse> getAll(String serviceId, String q) {
        String query = q == null ? null : q.toLowerCase();
        return professionalRepository.findAll().stream()
                .filter(p -> serviceId == null || serviceId.isBlank() || serviceId.equals(p.getServiceId()))
                .filter(p -> query == null || query.isBlank()
                        || p.getName().toLowerCase().contains(query)
                        || ServiceCatalog.nameOf(p.getServiceId()).toLowerCase().contains(query)
                        || (p.getLocation() != null && p.getLocation().toLowerCase().contains(query)))
                .map(this::toResponse)
                .toList();
    }

    public ProfessionalResponse getById(Long id) {
        return toResponse(findEntity(id));
    }

    public ProfessionalResponse upsertOwn(String email, ProfessionalRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        Professional professional = professionalRepository.findByUserId(user.getId())
                .orElseGet(Professional::new);

        professional.setUserId(user.getId());
        professional.setName(request.name());
        professional.setServiceId(request.serviceId());
        professional.setHourlyRate(request.hourlyRate());
        professional.setImage(request.image());
        professional.setLocation(request.location());
        professional.setDescription(request.description());
        professional.setExperience(request.experience());
        if (request.availability() != null) {
            professional.setAvailability(request.availability());
        }

        professional = professionalRepository.save(professional);

        if (user.getRole() != Role.PROFESSIONAL) {
            user.setRole(Role.PROFESSIONAL);
            userRepository.save(user);
        }

        return toResponse(professional);
    }

    Professional findEntity(Long id) {
        return professionalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesional no encontrado"));
    }

    private ProfessionalResponse toResponse(Professional p) {
        List<Review> reviews = reviewRepository.findByProfessionalId(p.getId());
        double avgRating = reviews.isEmpty()
                ? 0
                : reviews.stream().mapToInt(Review::getRating).average().orElse(0);

        return new ProfessionalResponse(
                p.getId(),
                p.getUserId(),
                p.getName(),
                ServiceCatalog.nameOf(p.getServiceId()),
                p.getServiceId(),
                Math.round(avgRating * 10) / 10.0,
                reviews.size(),
                p.getHourlyRate(),
                p.getImage(),
                p.getLocation(),
                p.getDescription(),
                p.getExperience(),
                p.isVerified(),
                p.getAvailability()
        );
    }
}

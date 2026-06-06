package com.encasa.services;

import com.encasa.models.Professional;
import com.encasa.models.User;
import com.encasa.professionals.dto.ProfessionalRequest;
import com.encasa.repositories.ProfessionalRepository;
import com.encasa.repositories.ServiceRepository;
import com.encasa.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;

    public ProfessionalService(ProfessionalRepository professionalRepository,
                               UserRepository userRepository,
                               ServiceRepository serviceRepository) {
        this.professionalRepository = professionalRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
    }

    public Professional getMyProfile(String email) {
        Long userId = findUser(email).getId();
        return professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professional profile not found"));
    }

    public Professional register(String email, ProfessionalRequest req) {
        if (req.serviceId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "serviceId is required");
        }

        User user = findUser(email);

        if (professionalRepository.findByUserId(user.getId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Professional profile already exists");
        }

        String serviceName = resolveServiceName(req.serviceId());

        Professional professional = new Professional();
        professional.setUserId(user.getId());
        professional.setName(req.name() != null ? req.name()
                : user.getName() != null ? user.getName()
                : user.getEmail());
        professional.setService(serviceName);
        professional.setServiceId(req.serviceId());
        professional.setHourlyRate(req.hourlyRate());
        professional.setImage(req.image());
        professional.setLocation(req.location());
        professional.setDescription(req.description());
        professional.setExperience(req.experience());
        professional.setAvailability(req.availability());
        professional.setRating(0.0);
        professional.setReviewCount(0);
        professional.setVerified(false);

        return professionalRepository.save(professional);
    }

    public Professional updateMyProfile(String email, ProfessionalRequest req) {
        Long userId = findUser(email).getId();
        Professional professional = professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professional profile not found"));

        if (req.name() != null)         professional.setName(req.name());
        if (req.serviceId() != null) {
            professional.setServiceId(req.serviceId());
            professional.setService(resolveServiceName(req.serviceId()));
        }
        if (req.hourlyRate() != null)   professional.setHourlyRate(req.hourlyRate());
        if (req.image() != null)        professional.setImage(req.image());
        if (req.location() != null)     professional.setLocation(req.location());
        if (req.description() != null)  professional.setDescription(req.description());
        if (req.experience() != null)   professional.setExperience(req.experience());
        if (req.availability() != null) professional.setAvailability(req.availability());

        return professionalRepository.save(professional);
    }

    public void deleteMyProfile(String email) {
        Long userId = findUser(email).getId();
        Professional professional = professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professional profile not found"));
        professionalRepository.delete(professional);
    }

    private String resolveServiceName(String serviceId) {
        return serviceRepository.findById(serviceId)
                .map(s -> s.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid serviceId: " + serviceId));
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}

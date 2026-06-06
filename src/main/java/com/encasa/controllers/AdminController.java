package com.encasa.controllers;

import com.encasa.admin.dto.*;
import com.encasa.models.Booking;
import com.encasa.models.Professional;
import com.encasa.models.Review;
import com.encasa.models.Service;
import com.encasa.models.User;
import com.encasa.repositories.BookingRepository;
import com.encasa.repositories.ProfessionalRepository;
import com.encasa.repositories.ServiceRepository;
import com.encasa.repositories.UserRepository;
import com.encasa.services.BookingService;
import com.encasa.services.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final ProfessionalRepository professionalRepository;
    private final ServiceRepository serviceRepository;
    private final BookingService bookingService;
    private final ReviewService reviewService;

    public AdminController(UserRepository userRepository,
                           ProfessionalRepository professionalRepository,
                           ServiceRepository serviceRepository,
                           BookingService bookingService,
                           ReviewService reviewService) {
        this.userRepository = userRepository;
        this.professionalRepository = professionalRepository;
        this.serviceRepository = serviceRepository;
        this.bookingService = bookingService;
        this.reviewService = reviewService;
    }

    // ── Users ─────────────────────────────────────────────────────────────────

    @GetMapping("/users")
    public List<AdminUserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponse)
                .toList();
    }

    @GetMapping("/users/{id}")
    public AdminUserResponse getUser(@PathVariable Long id) {
        return toUserResponse(findUser(id));
    }

    @PutMapping("/users/{id}/role")
    public AdminUserResponse updateRole(@PathVariable Long id,
                                        @RequestBody UpdateRoleRequest request) {
        User user = findUser(id);
        user.setRole(request.role());
        return toUserResponse(userRepository.save(user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.delete(findUser(id));
        return ResponseEntity.noContent().build();
    }

    // ── Professionals ─────────────────────────────────────────────────────────

    @GetMapping("/professionals")
    public List<Professional> listProfessionals() {
        return professionalRepository.findAll();
    }

    @PutMapping("/professionals/{id}")
    public Professional updateProfessional(@PathVariable Long id,
                                           @RequestBody AdminProfessionalUpdate req) {
        Professional p = professionalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professional not found"));

        if (req.name() != null)         p.setName(req.name());
        if (req.serviceId() != null) {
            Service svc = serviceRepository.findById(req.serviceId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid serviceId"));
            p.setServiceId(req.serviceId());
            p.setService(svc.getName());
        }
        if (req.hourlyRate() != null)   p.setHourlyRate(req.hourlyRate());
        if (req.image() != null)        p.setImage(req.image());
        if (req.location() != null)     p.setLocation(req.location());
        if (req.description() != null)  p.setDescription(req.description());
        if (req.experience() != null)   p.setExperience(req.experience());
        if (req.availability() != null) p.setAvailability(req.availability());
        if (req.verified() != null)     p.setVerified(req.verified());

        return professionalRepository.save(p);
    }

    @DeleteMapping("/professionals/{id}")
    public ResponseEntity<Void> deleteProfessional(@PathVariable Long id) {
        Professional p = professionalRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professional not found"));
        professionalRepository.delete(p);
        return ResponseEntity.noContent().build();
    }

    // ── Services ──────────────────────────────────────────────────────────────

    @PostMapping("/services")
    public ResponseEntity<Service> createService(@RequestBody ServiceRequest req) {
        if (serviceRepository.existsById(req.id())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Service id already exists");
        }
        Service service = new Service(req.id(), req.name(), req.description(), req.icon(), req.color());
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceRepository.save(service));
    }

    @PutMapping("/services/{id}")
    public Service updateService(@PathVariable String id, @RequestBody ServiceRequest req) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));
        Service updated = new Service(
                id,
                req.name() != null ? req.name() : service.getName(),
                req.description() != null ? req.description() : service.getDescription(),
                req.icon() != null ? req.icon() : service.getIcon(),
                req.color() != null ? req.color() : service.getColor()
        );
        return serviceRepository.save(updated);
    }

    @DeleteMapping("/services/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable String id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));
        serviceRepository.delete(service);
        return ResponseEntity.noContent().build();
    }

    // ── Bookings ──────────────────────────────────────────────────────────────

    @GetMapping("/bookings")
    public List<Booking> listBookings() {
        return bookingService.getAll();
    }

    // ── Reviews ───────────────────────────────────────────────────────────────

    @GetMapping("/reviews")
    public List<Review> listReviews() {
        return reviewService.getAll();
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.adminDelete(id);
        return ResponseEntity.noContent().build();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private AdminUserResponse toUserResponse(User u) {
        return new AdminUserResponse(u.getId(), u.getEmail(), u.getName(),
                u.getPhone(), u.getLocation(), u.getRole());
    }
}

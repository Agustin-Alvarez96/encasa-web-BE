package com.encasa.services;

import com.encasa.bookings.dto.BookingResponse;
import com.encasa.bookings.dto.ClientBookingResponse;
import com.encasa.bookings.dto.CreateBookingRequest;
import com.encasa.models.Booking;
import com.encasa.models.Booking.Status;
import com.encasa.models.Professional;
import com.encasa.models.User;
import com.encasa.repositories.BookingRepository;
import com.encasa.repositories.ProfessionalRepository;
import com.encasa.repositories.ServiceRepository;
import com.encasa.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ProfessionalRepository professionalRepository;
    private final ServiceRepository serviceRepository;

    public BookingService(BookingRepository bookingRepository,
                          UserRepository userRepository,
                          ProfessionalRepository professionalRepository,
                          ServiceRepository serviceRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.professionalRepository = professionalRepository;
        this.serviceRepository = serviceRepository;
    }

    public Booking create(String clientEmail, CreateBookingRequest req) {
        User client = findUser(clientEmail);
        Professional professional = professionalRepository.findById(req.professionalId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professional not found"));

        if (req.scheduledDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "scheduledDate is required");
        }

        Booking booking = new Booking();
        booking.setClientUserId(client.getId());
        booking.setProfessionalId(professional.getId());
        booking.setServiceId(professional.getServiceId());
        booking.setScheduledDate(req.scheduledDate());
        booking.setEstimatedHours(req.estimatedHours());
        booking.setNotes(req.notes());
        booking.setStatus(Status.PENDING);

        if (req.estimatedHours() != null && professional.getHourlyRate() != null) {
            booking.setTotalPrice(req.estimatedHours() * professional.getHourlyRate());
        }

        return bookingRepository.save(booking);
    }

    public List<ClientBookingResponse> getMyBookingsAsClient(String email) {
        Long userId = findUser(email).getId();
        List<Booking> bookings = bookingRepository.findByClientUserIdOrderByScheduledDateDesc(userId);

        List<Long> professionalIds = bookings.stream().map(Booking::getProfessionalId).distinct().collect(Collectors.toList());
        Map<Long, Professional> profById = professionalRepository.findAllById(professionalIds).stream()
                .collect(Collectors.toMap(Professional::getId, p -> p));

        Map<String, String> serviceNames = serviceRepository.findAll().stream()
                .collect(Collectors.toMap(s -> s.getId(), s -> s.getName()));

        return bookings.stream().map(b -> {
            Professional prof = profById.get(b.getProfessionalId());
            return new ClientBookingResponse(
                    b.getId(),
                    b.getProfessionalId(),
                    prof != null ? prof.getName() : "Profesional",
                    prof != null ? prof.getPhone() : null,
                    b.getServiceId(),
                    serviceNames.getOrDefault(b.getServiceId(), b.getServiceId()),
                    b.getScheduledDate(),
                    b.getStatus().name(),
                    b.getNotes(),
                    b.getEstimatedHours(),
                    b.getTotalPrice(),
                    b.getCreatedAt(),
                    b.getUpdatedAt()
            );
        }).collect(Collectors.toList());
    }

    public List<BookingResponse> getMyBookingsAsProfessional(String email) {
        Long userId = findUser(email).getId();
        Professional professional = professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professional profile not found"));

        List<Booking> bookings = bookingRepository.findByProfessionalIdOrderByScheduledDateDesc(professional.getId());

        List<Long> clientIds = bookings.stream().map(Booking::getClientUserId).distinct().collect(Collectors.toList());
        Map<Long, User> clientsById = userRepository.findAllById(clientIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        Map<String, String> serviceNames = serviceRepository.findAll().stream()
                .collect(Collectors.toMap(s -> s.getId(), s -> s.getName()));

        return bookings.stream().map(b -> {
            User client = clientsById.get(b.getClientUserId());
            return new BookingResponse(
                    b.getId(),
                    b.getClientUserId(),
                    client != null ? client.getName() : "Cliente",
                    client != null ? client.getEmail() : null,
                    b.getProfessionalId(),
                    b.getServiceId(),
                    serviceNames.getOrDefault(b.getServiceId(), b.getServiceId()),
                    b.getScheduledDate(),
                    b.getStatus().name(),
                    b.getNotes(),
                    b.getEstimatedHours(),
                    b.getTotalPrice(),
                    b.getCreatedAt(),
                    b.getUpdatedAt()
            );
        }).collect(Collectors.toList());
    }

    public Booking confirm(String professionalEmail, Long bookingId) {
        Booking booking = getBookingForProfessional(professionalEmail, bookingId);
        requireStatus(booking, Status.PENDING, "Only pending bookings can be confirmed");
        booking.setStatus(Status.CONFIRMED);
        return bookingRepository.save(booking);
    }

    public Booking cancel(String email, Long bookingId) {
        Booking booking = getBookingForParticipant(email, bookingId);
        if (booking.getStatus() == Status.COMPLETED || booking.getStatus() == Status.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking cannot be cancelled");
        }
        booking.setStatus(Status.CANCELLED);
        return bookingRepository.save(booking);
    }

    public Booking complete(String professionalEmail, Long bookingId) {
        Booking booking = getBookingForProfessional(professionalEmail, bookingId);
        requireStatus(booking, Status.CONFIRMED, "Only confirmed bookings can be completed");
        booking.setStatus(Status.COMPLETED);
        return bookingRepository.save(booking);
    }

    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Booking getBookingForProfessional(String email, Long bookingId) {
        Long userId = findUser(email).getId();
        Professional professional = professionalRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professional profile not found"));
        Booking booking = findBooking(bookingId);
        if (!booking.getProfessionalId().equals(professional.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return booking;
    }

    private Booking getBookingForParticipant(String email, Long bookingId) {
        Long userId = findUser(email).getId();
        Booking booking = findBooking(bookingId);
        boolean isClient = booking.getClientUserId().equals(userId);
        boolean isProfessional = professionalRepository.findByUserId(userId)
                .map(p -> p.getId().equals(booking.getProfessionalId()))
                .orElse(false);
        if (!isClient && !isProfessional) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return booking;
    }

    private void requireStatus(Booking booking, Status required, String message) {
        if (booking.getStatus() != required) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private Booking findBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}

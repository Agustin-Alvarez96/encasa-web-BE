package com.encasa.services;

import com.encasa.dto.BookingRequest;
import com.encasa.dto.BookingResponse;
import com.encasa.models.Booking;
import com.encasa.models.BookingStatus;
import com.encasa.models.Professional;
import com.encasa.models.User;
import com.encasa.repositories.BookingRepository;
import com.encasa.repositories.ProfessionalRepository;
import com.encasa.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ProfessionalRepository professionalRepository;
    private final UserRepository userRepository;

    @Value("${booking.auto-complete-days:5}")
    private long autoCompleteDays;

    public BookingService(BookingRepository bookingRepository,
                           ProfessionalRepository professionalRepository,
                           UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.professionalRepository = professionalRepository;
        this.userRepository = userRepository;
    }

    public BookingResponse create(String clientEmail, BookingRequest request) {
        User client = requireUser(clientEmail);
        Professional professional = professionalRepository.findById(request.professionalId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesional no encontrado"));

        if (professional.getUserId().equals(client.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No podés solicitarte un servicio a vos mismo");
        }

        Booking booking = new Booking();
        booking.setClientUserId(client.getId());
        booking.setProfessionalId(professional.getId());
        booking.setMessage(request.message());

        return toResponse(bookingRepository.save(booking));
    }

    public List<BookingResponse> getMine(String email) {
        User user = requireUser(email);

        List<Booking> bookings = new ArrayList<>(bookingRepository.findByClientUserId(user.getId()));
        professionalRepository.findByUserId(user.getId())
                .ifPresent(professional -> bookings.addAll(bookingRepository.findByProfessionalId(professional.getId())));

        return new LinkedHashSet<>(bookings).stream()
                .map(this::resolveAutoComplete)
                .map(this::toResponse)
                .toList();
    }

    public BookingResponse markInProgress(String professionalEmail, Long bookingId) {
        Booking booking = findEntity(bookingId);
        Professional professional = requireOwnedProfessional(professionalEmail, booking);

        if (booking.getStatus() != BookingStatus.REQUESTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La solicitud ya no está pendiente");
        }

        booking.setStatus(BookingStatus.IN_PROGRESS);
        return toResponse(bookingRepository.save(booking));
    }

    public BookingResponse confirmCompletion(String email, Long bookingId) {
        User user = requireUser(email);
        Booking booking = findEntity(bookingId);

        boolean isClient = booking.getClientUserId().equals(user.getId());
        boolean isProfessional = professionalRepository.findByUserId(user.getId())
                .map(p -> p.getId().equals(booking.getProfessionalId()))
                .orElse(false);

        if (!isClient && !isProfessional) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No participás de esta solicitud");
        }

        if (booking.getStatus() != BookingStatus.IN_PROGRESS && booking.getStatus() != BookingStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La solicitud todavía no está en proceso");
        }

        Instant now = Instant.now();
        if (isClient && booking.getClientConfirmedAt() == null) {
            booking.setClientConfirmedAt(now);
        }
        if (isProfessional && booking.getProfessionalConfirmedAt() == null) {
            booking.setProfessionalConfirmedAt(now);
        }

        if (booking.getClientConfirmedAt() != null && booking.getProfessionalConfirmedAt() != null) {
            booking.setStatus(BookingStatus.COMPLETED);
        }

        return toResponse(resolveAutoComplete(bookingRepository.save(booking)));
    }

    /**
     * Si una sola de las partes confirmó y pasaron más de {@code autoCompleteDays} días,
     * la solicitud se cierra sola (sin cron: se evalúa cada vez que se lee un booking).
     */
    Booking resolveAutoComplete(Booking booking) {
        if (booking.getStatus() != BookingStatus.IN_PROGRESS) {
            return booking;
        }

        Instant oneSidedConfirmation = null;
        if (booking.getClientConfirmedAt() != null && booking.getProfessionalConfirmedAt() == null) {
            oneSidedConfirmation = booking.getClientConfirmedAt();
        } else if (booking.getProfessionalConfirmedAt() != null && booking.getClientConfirmedAt() == null) {
            oneSidedConfirmation = booking.getProfessionalConfirmedAt();
        }

        if (oneSidedConfirmation != null
                && oneSidedConfirmation.plus(autoCompleteDays, ChronoUnit.DAYS).isBefore(Instant.now())) {
            booking.setStatus(BookingStatus.COMPLETED);
            return bookingRepository.save(booking);
        }

        return booking;
    }

    Booking findEntity(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));
    }

    private Professional requireOwnedProfessional(String email, Booking booking) {
        User user = requireUser(email);
        Professional professional = professionalRepository.findById(booking.getProfessionalId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profesional no encontrado"));

        if (!professional.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta solicitud no te pertenece");
        }
        return professional;
    }

    private User requireUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
    }

    BookingResponse toResponse(Booking b) {
        String clientEmail = userRepository.findById(b.getClientUserId())
                .map(User::getEmail)
                .orElse(null);

        return new BookingResponse(
                b.getId(),
                b.getClientUserId(),
                clientEmail,
                b.getProfessionalId(),
                b.getMessage(),
                b.getStatus().name(),
                b.getCreatedAt().toString(),
                b.getClientConfirmedAt() == null ? null : b.getClientConfirmedAt().toString(),
                b.getProfessionalConfirmedAt() == null ? null : b.getProfessionalConfirmedAt().toString()
        );
    }
}

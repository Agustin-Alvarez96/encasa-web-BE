package com.encasa.services;

import com.encasa.models.Notification;
import com.encasa.models.User;
import com.encasa.notifications.dto.NotificationResponse;
import com.encasa.repositories.NotificationRepository;
import com.encasa.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public void notifyNewBooking(Long professionalUserId, String clientName, Long bookingId) {
        create(professionalUserId, Notification.Type.NEW_BOOKING,
                clientName + " te envió una nueva solicitud", bookingId);
    }

    public void notifyBookingNeedsConfirmation(Long recipientUserId, Long bookingId) {
        create(recipientUserId, Notification.Type.BOOKING_NEEDS_YOUR_CONFIRMATION,
                "Confirmá que el trabajo se completó para poder seguir con la reserva", bookingId);
    }

    public void notifyBookingCompleted(Long clientUserId, String professionalName, Long bookingId) {
        create(clientUserId, Notification.Type.BOOKING_COMPLETED,
                "Tu solicitud con " + professionalName + " se completó. ¡Dejale una reseña!", bookingId);
    }

    public void notifyReviewReceived(Long professionalUserId, int rating, Long bookingId) {
        create(professionalUserId, Notification.Type.REVIEW_RECEIVED,
                "Recibiste una reseña nueva de " + rating + " estrellas", bookingId);
    }

    public List<NotificationResponse> getMyNotifications(String email) {
        Long userId = findUser(email).getId();
        return notificationRepository.findTop30ByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(NotificationResponse::from).collect(Collectors.toList());
    }

    public void markRead(String email, Long notificationId) {
        Long userId = findUser(email).getId();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificación no encontrada"));
        if (!notification.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllRead(String email) {
        Long userId = findUser(email).getId();
        List<Notification> unread = notificationRepository.findByUserIdAndReadFalse(userId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    private void create(Long userId, Notification.Type type, String message, Long bookingId) {
        if (userId == null) return;
        notificationRepository.save(new Notification(userId, type, message, bookingId));
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }
}

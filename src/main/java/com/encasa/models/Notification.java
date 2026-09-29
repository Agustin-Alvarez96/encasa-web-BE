package com.encasa.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    public enum Type { NEW_BOOKING, BOOKING_NEEDS_YOUR_CONFIRMATION, BOOKING_COMPLETED, REVIEW_RECEIVED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(nullable = false, length = 300)
    private String message;

    private Long bookingId;

    @Column(nullable = false)
    private boolean read = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Notification() {}

    public Notification(Long userId, Type type, String message, Long bookingId) {
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.bookingId = bookingId;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Type getType() { return type; }
    public String getMessage() { return message; }
    public Long getBookingId() { return bookingId; }
    public boolean isRead() { return read; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setRead(boolean read) { this.read = read; }
}

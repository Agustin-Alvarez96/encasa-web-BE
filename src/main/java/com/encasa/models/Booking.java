package com.encasa.models;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long clientUserId;

    @Column(nullable = false)
    private Long professionalId;

    @Column(length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.REQUESTED;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    private Instant clientConfirmedAt;

    private Instant professionalConfirmedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientUserId() {
        return clientUserId;
    }

    public void setClientUserId(Long clientUserId) {
        this.clientUserId = clientUserId;
    }

    public Long getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(Long professionalId) {
        this.professionalId = professionalId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getClientConfirmedAt() {
        return clientConfirmedAt;
    }

    public void setClientConfirmedAt(Instant clientConfirmedAt) {
        this.clientConfirmedAt = clientConfirmedAt;
    }

    public Instant getProfessionalConfirmedAt() {
        return professionalConfirmedAt;
    }

    public void setProfessionalConfirmedAt(Instant professionalConfirmedAt) {
        this.professionalConfirmedAt = professionalConfirmedAt;
    }
}

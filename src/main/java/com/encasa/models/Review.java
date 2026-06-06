package com.encasa.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long bookingId;

    @Column(nullable = false)
    private Long clientUserId;

    @Column(nullable = false)
    private Long professionalId;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 1000)
    private String comment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Review() {}

    public Long getId() { return id; }
    public Long getBookingId() { return bookingId; }
    public Long getClientUserId() { return clientUserId; }
    public Long getProfessionalId() { return professionalId; }
    public Integer getRating() { return rating; }
    public String getComment() { return comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public void setClientUserId(Long clientUserId) { this.clientUserId = clientUserId; }
    public void setProfessionalId(Long professionalId) { this.professionalId = professionalId; }
    public void setRating(Integer rating) { this.rating = rating; }
    public void setComment(String comment) { this.comment = comment; }
}

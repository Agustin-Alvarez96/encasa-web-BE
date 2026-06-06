package com.encasa.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    public enum Status { PENDING, CONFIRMED, CANCELLED, COMPLETED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long clientUserId;

    @Column(nullable = false)
    private Long professionalId;

    @Column(nullable = false)
    private String serviceId;

    @Column(nullable = false)
    private LocalDateTime scheduledDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(length = 500)
    private String notes;

    private Integer estimatedHours;
    private Integer totalPrice;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    public Booking() {}

    public Long getId() { return id; }
    public Long getClientUserId() { return clientUserId; }
    public Long getProfessionalId() { return professionalId; }
    public String getServiceId() { return serviceId; }
    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public Status getStatus() { return status; }
    public String getNotes() { return notes; }
    public Integer getEstimatedHours() { return estimatedHours; }
    public Integer getTotalPrice() { return totalPrice; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setClientUserId(Long clientUserId) { this.clientUserId = clientUserId; }
    public void setProfessionalId(Long professionalId) { this.professionalId = professionalId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }
    public void setStatus(Status status) { this.status = status; this.updatedAt = LocalDateTime.now(); }
    public void setNotes(String notes) { this.notes = notes; }
    public void setEstimatedHours(Integer estimatedHours) { this.estimatedHours = estimatedHours; }
    public void setTotalPrice(Integer totalPrice) { this.totalPrice = totalPrice; }
}

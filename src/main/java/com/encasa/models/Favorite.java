package com.encasa.models;

import jakarta.persistence.*;

@Entity
@Table(name = "favorites", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "professional_id"})
})
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "professional_id", nullable = false)
    private Long professionalId;

    public Favorite() {}

    public Favorite(Long userId, Long professionalId) {
        this.userId = userId;
        this.professionalId = professionalId;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getProfessionalId() { return professionalId; }
}

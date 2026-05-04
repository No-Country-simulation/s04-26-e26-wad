package com.opscore.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Incident
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    // Quién recibe
    @Column(nullable = false)
    private String assignedTo;

    // Quién asigna
    @Column(nullable = false)
    private String assignedBy;

    // Cuándo se asigna
    @Column(nullable = false)
    private LocalDateTime assignedAt;

    // Opcional pero MUY útil
    @PrePersist
    public void prePersist() {
        this.assignedAt = LocalDateTime.now();
    }

    // getters/setters
}


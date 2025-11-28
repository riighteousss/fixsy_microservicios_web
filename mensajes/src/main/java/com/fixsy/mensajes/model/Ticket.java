package com.fixsy.mensajes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "user_name")
    private String userName;

    @Column(nullable = false)
    private String asunto;

    @Column(nullable = false)
    private String categoria; // Consulta, Reclamo, Devolución, Problema Técnico, Otro

    @Column(nullable = false)
    private String estado; // Abierto, En Proceso, Resuelto, Cerrado

    @Column(nullable = false)
    private String prioridad; // Baja, Media, Alta, Urgente

    @Column(name = "assigned_to")
    private Long assignedTo; // ID del usuario de soporte asignado

    @Column(name = "assigned_name")
    private String assignedName; // Nombre del soporte asignado

    @Column(name = "order_id")
    private Long orderId; // Orden relacionada (opcional)

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (estado == null) estado = "Abierto";
        if (prioridad == null) prioridad = "Media";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}


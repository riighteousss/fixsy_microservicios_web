package com.fixsy.ordenes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "user_name")
    private String userName; // Nombre completo del comprador

    @Column(nullable = false)
    private String status; // Pendiente, Pagado, Enviado, Entregado, Cancelado

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "shipping_cost", precision = 10, scale = 2)
    private BigDecimal shippingCost;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "shipping_region")
    private String shippingRegion;

    @Column(name = "shipping_comuna")
    private String shippingComuna;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "payment_method")
    private String paymentMethod; // Transferencia, Tarjeta, etc.

    @Column(name = "payment_reference")
    private String paymentReference; // Número de transacción

    @Column(columnDefinition = "TEXT")
    private String notes; // Notas del cliente

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes; // Notas internas del admin

    @Column(name = "tracking_number")
    private String trackingNumber; // Número de seguimiento envío

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = "Pendiente";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}


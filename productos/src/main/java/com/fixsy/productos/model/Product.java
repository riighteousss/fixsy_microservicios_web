package com.fixsy.productos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "precio_oferta", precision = 10, scale = 2)
    private BigDecimal precioOferta;

    @Column(nullable = false)
    private Integer stock;

    @Column(columnDefinition = "TEXT")
    private String tags; // Separados por comas: "motor,mantenimiento,filtros"

    @Column(columnDefinition = "TEXT")
    private String imagen; // URL de imagen principal

    @Column(columnDefinition = "TEXT")
    private String images; // URLs separadas por comas para galería

    @Column
    private String categoria; // Categoría del producto

    @Column
    private String marca; // Marca del repuesto

    @Column
    private String sku; // Código único del producto

    @Column(name = "is_featured")
    private Boolean isFeatured = false; // Producto destacado

    @Column(name = "is_active")
    private Boolean isActive = true; // Producto activo/visible

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) isActive = true;
        if (isFeatured == null) isFeatured = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}


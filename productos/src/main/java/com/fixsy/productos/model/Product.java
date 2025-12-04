package com.fixsy.productos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "products",
        indexes = {
                @Index(name = "uk_products_sku", columnList = "sku", unique = true)
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String slug;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "descripcion_corta", columnDefinition = "TEXT")
    private String descripcionCorta;

    @Column(name = "descripcion_larga", columnDefinition = "TEXT")
    private String descripcionLarga;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioNormal;

    @Column(name = "precio_oferta", precision = 10, scale = 2)
    private BigDecimal precioOferta;

    @Column(nullable = false)
    private Integer stock;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> tags = new ArrayList<>();

    @Convert(converter = LongListConverter.class)
    @Column(name = "tag_ids", columnDefinition = "TEXT")
    private List<Long> tagIds = new ArrayList<>();

    @Column(name = "imagen", columnDefinition = "TEXT")
    private String imageUrl; // URL de imagen principal

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> images = new ArrayList<>(); // URLs separadas por comas para galeria

    @Column
    private String categoria; // Categoria del producto

    @Column(name = "category_id")
    private Long categoryId;

    @Column
    private String marca; // Marca del repuesto

    @Column(unique = true, nullable = false)
    private String sku; // Codigo unico del producto

    @Column(name = "is_featured")
    private Boolean isFeatured = false; // Producto destacado

    @Column(name = "is_active")
    private Boolean isActive = true; // Producto activo/visible

    @Column(name = "discount_percentage")
    private Integer discountPercentage;

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
        if (categoria == null) categoria = "Accesorios";
        if (discountPercentage == null) discountPercentage = 0;
        if (stock == null) stock = 0;
        if (slug == null || slug.isBlank()) slug = buildSlug(nombre);
        if (tags == null) tags = new ArrayList<>();
        if (tagIds == null) tagIds = new ArrayList<>();
        if (images == null) images = new ArrayList<>();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (slug == null || slug.isBlank()) slug = buildSlug(nombre);
        if (categoria == null) categoria = "Accesorios";
        if (tags == null) tags = new ArrayList<>();
        if (tagIds == null) tagIds = new ArrayList<>();
        if (images == null) images = new ArrayList<>();
        if (stock == null) stock = 0;
    }

    private String buildSlug(String base) {
        if (base == null) {
            return null;
        }
        return base.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
    }
}

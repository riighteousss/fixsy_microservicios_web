package com.fixsy.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar un producto de Fixsy Parts")
public class ProductDTO {
    @Schema(description = "ID del producto", example = "1")
    private Long id;

    @Schema(description = "Slug publico para URLs", example = "filtro-de-aceite")
    private String slug;

    @Schema(description = "Nombre del producto", example = "Filtro de aceite")
    private String nombre;

    @Schema(description = "Descripcion corta del producto", example = "Filtro de aceite estandar para motores 1.6-2.0L")
    private String descripcionCorta;

    @Schema(description = "Descripcion extendida del producto")
    private String descripcionLarga;

    @Schema(description = "Precio normal del producto en pesos", example = "9990")
    private BigDecimal precioNormal;

    @Schema(description = "Precio de oferta (si aplica)", example = "7990")
    private BigDecimal precioOferta;

    @Schema(description = "Porcentaje de descuento aplicado", example = "15")
    private Integer discountPercentage;

    @Schema(description = "Precio final calculado aplicando descuento", example = "8491.5")
    private BigDecimal finalPrice;

    @Schema(description = "Stock disponible", example = "12")
    private Integer stock;

    @Schema(description = "Tags/etiquetas del producto", example = "[\"motor\", \"mantenimiento\"]")
    private List<String> tags;

    @Schema(description = "IDs de tags asociados", example = "[1,2,3]")
    private List<Long> tagIds;

    @Schema(description = "URL de imagen principal", example = "https://example.com/filtro.jpg")
    private String imageUrl;

    @Schema(description = "URLs de imagenes adicionales")
    private List<String> images;

    @Schema(description = "Categoria del producto", example = "Filtros")
    private String categoria;

    @Schema(description = "ID de la categoria", example = "3")
    private Long categoryId;

    @Schema(description = "Marca del repuesto", example = "Bosch")
    private String marca;

    @Schema(description = "SKU/Codigo del producto", example = "FLT-001")
    private String sku;

    @Schema(description = "Producto destacado", example = "true")
    private Boolean destacado;

    @Schema(description = "Producto en oferta", example = "true")
    private Boolean oferta;

    @Schema(description = "Producto activo/visible", example = "true")
    private Boolean isActive;

    @Schema(description = "Fecha de creacion")
    private LocalDateTime createdAt;
}

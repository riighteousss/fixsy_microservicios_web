package com.fixsy.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear o actualizar un producto de Fixsy Parts")
public class ProductRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre del producto", example = "Filtro de aceite", required = true)
    private String nombre;

    @Schema(description = "Descripción del producto", example = "Filtro de aceite estándar para motores 1.6-2.0L")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    @Schema(description = "Precio del producto en pesos", example = "9990", required = true)
    private BigDecimal precio;

    @Min(value = 0, message = "El precio de oferta no puede ser negativo")
    @Schema(description = "Precio de oferta (opcional)", example = "7990")
    private BigDecimal precioOferta;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Schema(description = "Stock disponible", example = "12", required = true)
    private Integer stock;

    @Schema(description = "Tags/etiquetas del producto", example = "[\"motor\", \"mantenimiento\"]")
    private List<String> tags;

    @Schema(description = "URL de imagen principal", example = "https://example.com/filtro.jpg")
    private String imagen;

    @Schema(description = "URLs de imágenes adicionales")
    private List<String> images;

    @Schema(description = "Categoría del producto", example = "Filtros")
    private String categoria;

    @Schema(description = "Marca del repuesto", example = "Bosch")
    private String marca;

    @Schema(description = "SKU/Código del producto", example = "FLT-001")
    private String sku;

    @Schema(description = "Marcar como producto destacado", example = "false")
    private Boolean isFeatured = false;

    @Schema(description = "Producto activo/visible", example = "true")
    private Boolean isActive = true;
}


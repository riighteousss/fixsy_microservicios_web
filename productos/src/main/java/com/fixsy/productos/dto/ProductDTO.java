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
    
    @Schema(description = "Nombre del producto", example = "Filtro de aceite")
    private String nombre;
    
    @Schema(description = "Descripción del producto", example = "Filtro de aceite estándar para motores 1.6-2.0L")
    private String descripcion;
    
    @Schema(description = "Precio del producto en pesos", example = "9990")
    private BigDecimal precio;
    
    @Schema(description = "Precio de oferta (si aplica)", example = "7990")
    private BigDecimal precioOferta;
    
    @Schema(description = "Stock disponible", example = "12")
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
    
    @Schema(description = "Producto destacado", example = "true")
    private Boolean isFeatured;
    
    @Schema(description = "Producto activo/visible", example = "true")
    private Boolean isActive;
    
    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;
}


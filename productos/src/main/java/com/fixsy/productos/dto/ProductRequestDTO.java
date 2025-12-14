package com.fixsy.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear o actualizar un producto en el dashboard de admin")
public class ProductRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre del producto", example = "Filtro de aceite", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Descripcion extendida del producto", example = "Detalles tecnicos y compatibilidades")
    private String description;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    @Schema(description = "Precio base del producto", example = "9990", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal price;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Schema(description = "Stock disponible", example = "12", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer stock;

    @Schema(description = "ID de la categoria asociada", example = "3")
    private Long categoryId;

    @Schema(description = "IDs de tags asociados", example = "[1,2,3]")
    private List<Long> tagIds;

    @Schema(description = "Porcentaje de descuento a aplicar", example = "15")
    private Integer discountPercentage;

    @Schema(description = "SKU/Codigo del producto", example = "FLT-001")
    private String sku;

    @Schema(description = "Marcar como producto destacado", example = "false")
    private Boolean isFeatured = false;

    @Schema(description = "Producto activo/visible", example = "true")
    private Boolean isActive = true;

    @Schema(description = "URL de la imagen principal", example = "https://images.unsplash.com/photo-...")
    private String imageUrl;

    // Métodos de compatibilidad con naming previo (nombre/precioNormal/precioOferta)
    public String getNombre() { return name; }
    public void setNombre(String nombre) { this.name = nombre; }
    public BigDecimal getPrecioNormal() { return price; }
    public void setPrecioNormal(BigDecimal precioNormal) { this.price = precioNormal; }
    public BigDecimal getPrecioOferta() { return null; }
    public void setPrecioOferta(BigDecimal ignored) { /* sin uso: se migra a discountPercentage */ }
    public String getDescripcion() { return description; }
    public void setDescripcion(String descripcion) { this.description = descripcion; }
    public String getDescripcionCorta() { return description; }
    public void setDescripcionCorta(String descripcionCorta) { this.description = descripcionCorta; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}

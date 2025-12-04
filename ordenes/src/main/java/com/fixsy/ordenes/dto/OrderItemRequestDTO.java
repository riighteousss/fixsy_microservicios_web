package com.fixsy.ordenes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para agregar un item a una orden")
public class OrderItemRequestDTO {
    @NotNull(message = "El ID del producto es obligatorio")
    @Schema(description = "ID del producto", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Schema(description = "Nombre del producto", example = "Filtro de aceite", requiredMode = Schema.RequiredMode.REQUIRED)
    private String productName;

    @Schema(description = "SKU del producto", example = "FLT-001")
    private String productSku;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad minima es 1")
    @Schema(description = "Cantidad", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    @Schema(description = "Precio normal del producto", example = "9990")
    private BigDecimal precio;

    @Schema(description = "Precio de oferta (si aplica)", example = "7990")
    private BigDecimal precioOferta;

    @NotNull(message = "El precio unitario es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    @Schema(description = "Precio unitario", example = "9990", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal unitPrice;
}

package com.fixsy.ordenes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar un item de una orden")
public class OrderItemDTO {
    @Schema(description = "ID del item", example = "1")
    private Long id;
    
    @Schema(description = "ID del producto", example = "5")
    private Long productId;
    
    @Schema(description = "Nombre del producto", example = "Filtro de aceite")
    private String productName;
    
    @Schema(description = "SKU del producto", example = "FLT-001")
    private String productSku;
    
    @Schema(description = "Cantidad", example = "2")
    private Integer quantity;

    @Schema(description = "Precio unitario original", example = "9990")
    private BigDecimal originalUnitPrice;
    
    @Schema(description = "Precio unitario", example = "9990")
    private BigDecimal unitPrice;

    @Schema(description = "Descuento por unidad", example = "1000")
    private BigDecimal discountUnitAmount;
    
    @Schema(description = "Subtotal del item", example = "19980")
    private BigDecimal subtotal;
}

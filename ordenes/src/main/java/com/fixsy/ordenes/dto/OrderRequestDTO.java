package com.fixsy.ordenes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear una orden de compra")
public class OrderRequestDTO {
    @NotNull(message = "El ID de usuario es obligatorio")
    @Schema(description = "ID del usuario comprador", example = "1", required = true)
    private Long userId;

    @NotBlank(message = "El email es obligatorio")
    @Schema(description = "Email del usuario", example = "cliente@email.com", required = true)
    private String userEmail;

    @Schema(description = "Nombre del comprador", example = "Juan Pérez")
    private String userName;

    @NotEmpty(message = "La orden debe tener al menos un item")
    @Schema(description = "Items de la orden", required = true)
    private List<OrderItemRequestDTO> items;

    @NotBlank(message = "La dirección de envío es obligatoria")
    @Schema(description = "Dirección de envío", example = "Av. Principal 123, Depto 45", required = true)
    private String shippingAddress;

    @Schema(description = "Región de envío", example = "Metropolitana")
    private String shippingRegion;

    @Schema(description = "Comuna de envío", example = "Santiago")
    private String shippingComuna;

    @Schema(description = "Teléfono de contacto", example = "+56912345678")
    private String contactPhone;

    @Schema(description = "Costo de envío (se calcula si no se proporciona)", example = "3990")
    private BigDecimal shippingCost;

    @Schema(description = "Método de pago", example = "Transferencia")
    private String paymentMethod;

    @Schema(description = "Notas del cliente")
    private String notes;
}


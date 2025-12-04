package com.fixsy.ordenes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @Schema(description = "ID del usuario comprador", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @NotBlank(message = "El email es obligatorio")
    @Schema(description = "Email del usuario", example = "cliente@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userEmail;

    @Schema(description = "Nombre del comprador", example = "Juan Perez")
    private String userName;

    @NotEmpty(message = "La orden debe tener al menos un item")
    @Schema(description = "Items de la orden", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<OrderItemRequestDTO> items;

    @NotBlank(message = "La direccion de envio es obligatoria")
    @Schema(description = "Direccion de envio", example = "Av. Principal 123, Depto 45", requiredMode = Schema.RequiredMode.REQUIRED)
    private String shippingAddress;

    @Schema(description = "Region de envio", example = "Metropolitana")
    private String shippingRegion;

    @Schema(description = "Comuna de envio", example = "Santiago")
    private String shippingComuna;

    @Schema(description = "Telefono de contacto", example = "+56912345678")
    private String contactPhone;

    @Schema(description = "Costo de envio (se calcula si no se proporciona)", example = "3990")
    private BigDecimal shippingCost;

    @Schema(description = "Metodo de pago", example = "Transferencia")
    private String paymentMethod;

    @Schema(description = "Notas del cliente")
    private String notes;
}

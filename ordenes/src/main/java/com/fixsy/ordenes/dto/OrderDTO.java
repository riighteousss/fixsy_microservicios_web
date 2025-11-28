package com.fixsy.ordenes.dto;

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
@Schema(description = "DTO para representar una orden de compra de Fixsy Parts")
public class OrderDTO {
    @Schema(description = "ID de la orden", example = "1")
    private Long id;
    
    @Schema(description = "ID del usuario comprador", example = "1")
    private Long userId;
    
    @Schema(description = "Email del usuario", example = "cliente@email.com")
    private String userEmail;
    
    @Schema(description = "Nombre del comprador", example = "Juan Pérez")
    private String userName;
    
    @Schema(description = "Estado de la orden", example = "Pendiente", 
            allowableValues = {"Pendiente", "Pagado", "Enviado", "Entregado", "Cancelado"})
    private String status;
    
    @Schema(description = "Subtotal de productos", example = "29990")
    private BigDecimal subtotal;
    
    @Schema(description = "Costo de envío", example = "3990")
    private BigDecimal shippingCost;
    
    @Schema(description = "Total de la orden", example = "33980")
    private BigDecimal total;
    
    @Schema(description = "Dirección de envío", example = "Av. Principal 123, Depto 45")
    private String shippingAddress;
    
    @Schema(description = "Región de envío", example = "Metropolitana")
    private String shippingRegion;
    
    @Schema(description = "Comuna de envío", example = "Santiago")
    private String shippingComuna;
    
    @Schema(description = "Teléfono de contacto", example = "+56912345678")
    private String contactPhone;
    
    @Schema(description = "Método de pago", example = "Transferencia")
    private String paymentMethod;
    
    @Schema(description = "Referencia de pago", example = "TRX-123456")
    private String paymentReference;
    
    @Schema(description = "Notas del cliente")
    private String notes;
    
    @Schema(description = "Número de seguimiento de envío")
    private String trackingNumber;
    
    @Schema(description = "Items de la orden")
    private List<OrderItemDTO> items;
    
    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;
    
    @Schema(description = "Fecha de pago")
    private LocalDateTime paidAt;
    
    @Schema(description = "Fecha de envío")
    private LocalDateTime shippedAt;
    
    @Schema(description = "Fecha de entrega")
    private LocalDateTime deliveredAt;
}


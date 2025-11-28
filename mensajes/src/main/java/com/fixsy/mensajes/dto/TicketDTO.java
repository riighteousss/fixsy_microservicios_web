package com.fixsy.mensajes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar un ticket de soporte")
public class TicketDTO {
    @Schema(description = "ID del ticket", example = "1")
    private Long id;

    @Schema(description = "ID del usuario que creó el ticket", example = "1")
    private Long userId;

    @Schema(description = "Email del usuario", example = "cliente@email.com")
    private String userEmail;

    @Schema(description = "Nombre del usuario", example = "Juan Pérez")
    private String userName;

    @Schema(description = "Asunto del ticket", example = "Problema con mi pedido")
    private String asunto;

    @Schema(description = "Categoría del ticket", example = "Reclamo",
            allowableValues = {"Consulta", "Reclamo", "Devolución", "Problema Técnico", "Otro"})
    private String categoria;

    @Schema(description = "Estado del ticket", example = "Abierto",
            allowableValues = {"Abierto", "En Proceso", "Resuelto", "Cerrado"})
    private String estado;

    @Schema(description = "Prioridad del ticket", example = "Media",
            allowableValues = {"Baja", "Media", "Alta", "Urgente"})
    private String prioridad;

    @Schema(description = "ID del soporte asignado", example = "5")
    private Long assignedTo;

    @Schema(description = "Nombre del soporte asignado", example = "María García")
    private String assignedName;

    @Schema(description = "ID de la orden relacionada (opcional)", example = "123")
    private Long orderId;

    @Schema(description = "Mensajes del ticket")
    private List<MessageDTO> mensajes;

    @Schema(description = "Cantidad de mensajes no leídos", example = "2")
    private Integer unreadCount;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización")
    private LocalDateTime updatedAt;

    @Schema(description = "Fecha de cierre")
    private LocalDateTime closedAt;
}


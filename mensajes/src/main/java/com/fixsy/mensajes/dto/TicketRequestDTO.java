package com.fixsy.mensajes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear un ticket de soporte")
public class TicketRequestDTO {
    @NotNull(message = "El ID de usuario es obligatorio")
    @Schema(description = "ID del usuario", example = "1", required = true)
    private Long userId;

    @NotBlank(message = "El email es obligatorio")
    @Schema(description = "Email del usuario", example = "cliente@email.com", required = true)
    private String userEmail;

    @Schema(description = "Nombre del usuario", example = "Juan Pérez")
    private String userName;

    @NotBlank(message = "El asunto es obligatorio")
    @Schema(description = "Asunto del ticket", example = "Problema con mi pedido", required = true)
    private String asunto;

    @NotBlank(message = "La categoría es obligatoria")
    @Schema(description = "Categoría del ticket", example = "Reclamo", required = true,
            allowableValues = {"Consulta", "Reclamo", "Devolución", "Problema Técnico", "Otro"})
    private String categoria;

    @Schema(description = "Prioridad del ticket", example = "Media",
            allowableValues = {"Baja", "Media", "Alta", "Urgente"})
    private String prioridad = "Media";

    @Schema(description = "ID de la orden relacionada (opcional)", example = "123")
    private Long orderId;

    @NotBlank(message = "El mensaje inicial es obligatorio")
    @Schema(description = "Mensaje inicial del ticket", example = "Hola, tengo un problema con mi pedido...", required = true)
    private String mensajeInicial;
}


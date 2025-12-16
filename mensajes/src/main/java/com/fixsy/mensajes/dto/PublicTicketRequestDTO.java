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
@Schema(description = "DTO para crear un ticket público (invitado)")
public class PublicTicketRequestDTO {
    @NotNull(message = "El ID de la orden es obligatorio")
    @Schema(description = "ID de la orden asociada a la boleta", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;

    @NotBlank(message = "El email del cliente es obligatorio")
    @Schema(description = "Email del usuario", example = "cliente@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userEmail;

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre del usuario", example = "Invitado", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    @Schema(description = "Prioridad del ticket", example = "Alta",
            allowableValues = {"Baja", "Media", "Alta", "Urgente"})
    private String prioridad = "Alta";

    @Schema(description = "Notas opcionales que acompañan la solicitud de boleta")
    private String note;
}

package com.fixsy.mensajes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear un ticket público (invitado)")
public class PublicTicketRequestDTO {
    @NotBlank(message = "El email es obligatorio")
    @Schema(description = "Email del usuario", example = "cliente@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userEmail;

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre del usuario", example = "Invitado")
    private String userName;

    @NotBlank(message = "El asunto es obligatorio")
    @Schema(description = "Asunto del ticket", example = "Consulta desde formulario", requiredMode = Schema.RequiredMode.REQUIRED)
    private String asunto;

    @Schema(description = "Categoria del ticket", example = "Consulta",
            allowableValues = {"Consulta", "Reclamo", "Devolucion", "Problema Tecnico", "Otro"})
    private String categoria = "Consulta";

    @Schema(description = "Prioridad del ticket", example = "Media",
            allowableValues = {"Baja", "Media", "Alta", "Urgente"})
    private String prioridad = "Media";

    @Schema(description = "Mensaje inicial del ticket", example = "Necesito ayuda con mi compra", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El mensaje inicial es obligatorio")
    private String mensajeInicial;
}

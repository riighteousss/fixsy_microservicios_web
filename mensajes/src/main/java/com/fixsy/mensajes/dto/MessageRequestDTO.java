package com.fixsy.mensajes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para enviar un mensaje en un ticket")
public class MessageRequestDTO {
    @NotNull(message = "El ID del ticket es obligatorio")
    @Schema(description = "ID del ticket", example = "1", required = true)
    private Long ticketId;

    @NotNull(message = "El ID del remitente es obligatorio")
    @Schema(description = "ID del remitente", example = "1", required = true)
    private Long senderId;

    @NotBlank(message = "El email del remitente es obligatorio")
    @Schema(description = "Email del remitente", example = "cliente@email.com", required = true)
    private String senderEmail;

    @Schema(description = "Nombre del remitente", example = "Juan Pérez")
    private String senderName;

    @NotBlank(message = "El rol del remitente es obligatorio")
    @Schema(description = "Rol del remitente", example = "Usuario", required = true,
            allowableValues = {"Usuario", "Soporte", "Admin"})
    private String senderRole;

    @NotBlank(message = "El contenido del mensaje es obligatorio")
    @Schema(description = "Contenido del mensaje", example = "Gracias por su respuesta...", required = true)
    private String contenido;

    @Schema(description = "URLs de archivos adjuntos")
    private List<String> adjuntos;
}


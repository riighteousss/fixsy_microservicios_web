package com.fixsy.mensajes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para enviar un mensaje en un ticket")
public class MessageRequestDTO {
    @Schema(description = "ID del ticket", example = "1")
    private Long ticketId;

    @Schema(description = "ID del remitente", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long senderId;

    @NotBlank(message = "El email del remitente es obligatorio")
    @Schema(description = "Email del remitente", example = "cliente@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String senderEmail;

    @Schema(description = "Nombre del remitente", example = "Juan Pérez")
    private String senderName;

    @NotBlank(message = "El rol del remitente es obligatorio")
    @Schema(description = "Rol del remitente", example = "Usuario", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"Usuario", "Soporte", "Admin"})
    private String senderRole;

    @NotBlank(message = "El contenido del mensaje es obligatorio")
    @Schema(description = "Contenido del mensaje", example = "Gracias por su respuesta...", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contenido;

    @Schema(description = "URLs de archivos adjuntos")
    private List<String> adjuntos;
}

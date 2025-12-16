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
@Schema(description = "DTO para representar un mensaje")
public class MessageDTO {
    @Schema(description = "ID del mensaje", example = "1")
    private Long id;

    @Schema(description = "ID del ticket", example = "1")
    private Long ticketId;

    @Schema(description = "ID del remitente", example = "1")
    private Long senderId;

    @Schema(description = "Email del remitente", example = "cliente@email.com")
    private String senderEmail;

    @Schema(description = "Nombre del remitente", example = "Juan Pérez")
    private String senderName;

    @Schema(description = "Rol del remitente", example = "Usuario",
            allowableValues = {"Usuario", "Soporte", "Admin"})
    private String senderRole;

    @Schema(description = "Contenido del mensaje", example = "Hola, tengo un problema con mi pedido...")
    private String contenido;

    @Schema(description = "URLs de archivos adjuntos")
    private List<String> adjuntos;

    @Schema(description = "Indica si el mensaje fue leído", example = "false")
    private Boolean isRead;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Indica si el mensaje es interno (solo Soporte/Admin)", example = "false")
    private Boolean internal;
}

package com.fixsy.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear o actualizar un usuario de Fixsy Parts")
public class UserRequestDTO {
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Schema(description = "Email del usuario (el rol se asigna automáticamente según el dominio)", example = "usuario@example.com", required = true)
    private String email;

    @Schema(description = "Contraseña del usuario (opcional en actualizaciones, requerida al crear)", example = "password123")
    private String password;

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre del usuario", example = "Juan", required = true)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Schema(description = "Apellido del usuario", example = "Pérez", required = true)
    private String apellido;

    @Schema(description = "Teléfono del usuario", example = "+56912345678")
    private String phone;

    @Schema(description = "ID del rol (opcional, se determina por dominio de email si no se proporciona)", example = "1")
    private Long roleId;

    @Schema(description = "Estado del usuario", example = "Activo", allowableValues = {"Activo", "Bloqueado", "Suspendido"}, defaultValue = "Activo")
    private String status = "Activo";

    @Schema(description = "URL de la foto de perfil", example = "https://example.com/foto.jpg")
    private String profilePic;

    @Schema(description = "Fecha hasta la cual está suspendido (solo si status es Suspendido)")
    private LocalDateTime suspensionHasta;
}

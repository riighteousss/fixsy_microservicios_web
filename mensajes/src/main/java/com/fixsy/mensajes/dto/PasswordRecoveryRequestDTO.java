package com.fixsy.mensajes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para solicitar recuperacion de contrasena mediante ticket")
public class PasswordRecoveryRequestDTO {
    @Schema(description = "ID del usuario (opcional si no esta logueado)", example = "12")
    private Long userId;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es valido")
    @Schema(description = "Email del usuario", example = "usuario@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userEmail;

    @Schema(description = "Nombre del usuario", example = "Juan Perez")
    private String userName;
}

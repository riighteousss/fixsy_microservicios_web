package com.fixsy.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para login de usuario")
public class LoginRequestDTO {
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email invalido")
    @Schema(description = "Email del usuario", example = "usuario@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "La contrasena es obligatoria")
    @Schema(description = "Contrasena del usuario", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}

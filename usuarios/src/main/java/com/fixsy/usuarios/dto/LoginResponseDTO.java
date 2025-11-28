package com.fixsy.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta para login exitoso")
public class LoginResponseDTO {
    @Schema(description = "Indica si el login fue exitoso")
    private boolean success;
    
    @Schema(description = "Mensaje de respuesta")
    private String message;
    
    @Schema(description = "Datos del usuario autenticado")
    private UserDTO user;
}


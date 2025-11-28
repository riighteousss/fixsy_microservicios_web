package com.fixsy.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar un usuario de Fixsy Parts")
public class UserDTO {
    @Schema(description = "ID del usuario", example = "1")
    private Long id;
    
    @Schema(description = "Email del usuario", example = "usuario@example.com")
    private String email;
    
    @Schema(description = "Nombre del usuario", example = "Juan")
    private String nombre;
    
    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String apellido;
    
    @Schema(description = "Teléfono del usuario", example = "+56912345678")
    private String phone;
    
    @Schema(description = "Rol del usuario")
    private RoleDTO role;
    
    @Schema(description = "Estado del usuario", example = "Activo", allowableValues = {"Activo", "Bloqueado", "Suspendido"})
    private String status;
    
    @Schema(description = "URL de la foto de perfil", example = "https://example.com/foto.jpg")
    private String profilePic;
    
    @Schema(description = "Fecha hasta la cual está suspendido (si aplica)")
    private LocalDateTime suspensionHasta;
    
    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;
}

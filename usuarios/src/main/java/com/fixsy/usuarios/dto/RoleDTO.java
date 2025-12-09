package com.fixsy.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para representar un rol de usuario")
public class RoleDTO {
    @Schema(description = "ID del rol", example = "1")
    private Long id;
    
    @Schema(description = "Nombre del rol", example = "Usuario", allowableValues = {"Usuario", "Admin", "Cliente"})
    private String nombre;
    
    @Schema(description = "Descripción del rol", example = "Cliente normal de la tienda")
    private String descripcion;
    
    @Schema(description = "Dominio de email asociado", example = "admin.fixsy.com")
    private String emailDomain;
}


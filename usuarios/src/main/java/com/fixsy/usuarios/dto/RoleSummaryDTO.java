package com.fixsy.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Rol asociado al usuario en respuestas resumidas")
public class RoleSummaryDTO {
    @Schema(description = "ID del rol", example = "2")
    private Long id;

    @Schema(description = "Nombre del rol", example = "Administrador")
    private String name;

    @Schema(description = "Descripcion del rol", example = "Administrador con acceso completo al sistema")
    private String description;
}

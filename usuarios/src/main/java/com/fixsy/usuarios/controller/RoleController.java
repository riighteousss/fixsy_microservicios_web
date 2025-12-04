package com.fixsy.usuarios.controller;

import com.fixsy.usuarios.dto.RoleDTO;
import com.fixsy.usuarios.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/roles", "/api/roles"})
@CrossOrigin(origins = "*")
@Tag(name = "Role Controller", description = "API para consultar roles de usuario")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping
    @Operation(summary = "Obtener todos los roles disponibles")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener rol por ID")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @GetMapping("/nombre/{nombre}")
    @Operation(summary = "Obtener rol por nombre")
    public ResponseEntity<RoleDTO> getRoleByNombre(
            @PathVariable 
            @io.swagger.v3.oas.annotations.Parameter(description = "Nombre del rol", example = "Usuario")
            String nombre) {
        return ResponseEntity.ok(roleService.getRoleByNombre(nombre));
    }
}

package com.fixsy.usuarios.controller;

import com.fixsy.usuarios.dto.LoginRequestDTO;
import com.fixsy.usuarios.dto.LoginResponseDTO;
import com.fixsy.usuarios.dto.UserDTO;
import com.fixsy.usuarios.dto.UserRequestDTO;
import com.fixsy.usuarios.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Tag(name = "User Controller", description = "API para gestión de usuarios de Fixsy Parts")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Obtener usuario por email")
    public ResponseEntity<UserDTO> getUserByEmail(
            @PathVariable 
            @io.swagger.v3.oas.annotations.Parameter(description = "Email del usuario", example = "usuario@example.com")
            String email) {
        try {
            String decodedEmail = java.net.URLDecoder.decode(email, java.nio.charset.StandardCharsets.UTF_8);
            return ResponseEntity.ok(userService.getUserByEmail(decodedEmail));
        } catch (Exception e) {
            return ResponseEntity.ok(userService.getUserByEmail(email));
        }
    }

    @GetMapping("/role/{roleName}")
    @Operation(summary = "Obtener usuarios por nombre de rol")
    public ResponseEntity<List<UserDTO>> getUsersByRole(
            @PathVariable 
            @io.swagger.v3.oas.annotations.Parameter(description = "Nombre del rol", example = "Usuario")
            String roleName) {
        return ResponseEntity.ok(userService.getUsersByRole(roleName));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Obtener usuarios por estado")
    public ResponseEntity<List<UserDTO>> getUsersByStatus(
            @PathVariable 
            @io.swagger.v3.oas.annotations.Parameter(description = "Estado del usuario", example = "Activo")
            String status) {
        return ResponseEntity.ok(userService.getUsersByStatus(status));
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuario")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = userService.login(loginRequest);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario (el rol se asigna automáticamente por dominio de email)")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserRequestDTO userRequest) {
        // Validaciones
        if (userRequest.getEmail() == null || userRequest.getEmail().isBlank()) {
            throw new RuntimeException("El email es obligatorio");
        }
        if (!userRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Formato de email inválido");
        }
        if (userRequest.getNombre() == null || userRequest.getNombre().isBlank()) {
            throw new RuntimeException("El nombre es obligatorio");
        }
        if (userRequest.getApellido() == null || userRequest.getApellido().isBlank()) {
            throw new RuntimeException("El apellido es obligatorio");
        }
        if (userRequest.getPassword() == null || userRequest.getPassword().isEmpty()) {
            throw new RuntimeException("La contraseña es obligatoria");
        }
        if (userRequest.getPassword().length() < 8) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres");
        }
        
        // El rol se determina automáticamente por dominio de email
        // No forzamos roleId para permitir la asignación automática
        userRequest.setStatus("Activo");
        
        return new ResponseEntity<>(userService.createUser(userRequest), HttpStatus.CREATED);
    }

    @PostMapping
    @Operation(summary = "Crear nuevo usuario (Admin puede especificar rol)")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserRequestDTO userRequest) {
        if (userRequest.getEmail() == null || userRequest.getEmail().isBlank()) {
            throw new RuntimeException("El email es obligatorio");
        }
        if (!userRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Formato de email inválido");
        }
        if (userRequest.getNombre() == null || userRequest.getNombre().isBlank()) {
            throw new RuntimeException("El nombre es obligatorio");
        }
        if (userRequest.getApellido() == null || userRequest.getApellido().isBlank()) {
            throw new RuntimeException("El apellido es obligatorio");
        }
        if (userRequest.getPassword() == null || userRequest.getPassword().isEmpty()) {
            throw new RuntimeException("La contraseña es obligatoria al crear un usuario");
        }
        if (userRequest.getPassword().length() < 8) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres");
        }
        return new ResponseEntity<>(userService.createUser(userRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserRequestDTO userRequest) {
        if (userRequest.getEmail() == null || userRequest.getEmail().isBlank()) {
            throw new RuntimeException("El email es obligatorio");
        }
        if (!userRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Formato de email inválido");
        }
        if (userRequest.getNombre() == null || userRequest.getNombre().isBlank()) {
            throw new RuntimeException("El nombre es obligatorio");
        }
        if (userRequest.getApellido() == null || userRequest.getApellido().isBlank()) {
            throw new RuntimeException("El apellido es obligatorio");
        }
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty() && 
            userRequest.getPassword().length() < 8) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres");
        }
        return ResponseEntity.ok(userService.updateUser(id, userRequest));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Actualizar estado del usuario (Activo/Bloqueado/Suspendido)")
    public ResponseEntity<UserDTO> updateUserStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) LocalDateTime suspensionHasta) {
        return ResponseEntity.ok(userService.updateUserStatus(id, status, suspensionHasta));
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "Actualizar rol del usuario (Solo Admin)")
    public ResponseEntity<UserDTO> updateUserRole(
            @PathVariable Long id,
            @RequestParam Long roleId) {
        return ResponseEntity.ok(userService.updateUserRole(id, roleId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

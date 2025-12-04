package com.fixsy.usuarios.controller;

import com.fixsy.usuarios.dto.LoginRequestDTO;
import com.fixsy.usuarios.dto.LoginResponseDTO;
import com.fixsy.usuarios.dto.UserDTO;
import com.fixsy.usuarios.dto.UserRequestDTO;
import com.fixsy.usuarios.model.User;
import com.fixsy.usuarios.repository.UserRepository;
import com.fixsy.usuarios.service.UserImageStorageService;
import com.fixsy.usuarios.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping({"/api/v1/users", "/api/users"})
@CrossOrigin(origins = "*")
@Tag(name = "User Controller", description = "API para gestion de usuarios de Fixsy Parts")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserImageStorageService userImageStorageService;

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios con su rol (incluye roleId y objeto role)")
    @PreAuthorize("hasAnyRole('Admin','Soporte')")
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
    @Operation(summary = "Registrar nuevo usuario (el rol se asigna automaticamente por dominio de email)")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserRequestDTO userRequest) {
        // Validaciones
        if (userRequest.getEmail() == null || userRequest.getEmail().isBlank()) {
            throw new RuntimeException("El email es obligatorio");
        }
        if (!userRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Formato de email invalido");
        }
        if (userRequest.getNombre() == null || userRequest.getNombre().isBlank()) {
            throw new RuntimeException("El nombre es obligatorio");
        }
        if (userRequest.getApellido() == null || userRequest.getApellido().isBlank()) {
            throw new RuntimeException("El apellido es obligatorio");
        }
        if (userRequest.getPassword() == null || userRequest.getPassword().isEmpty()) {
            throw new RuntimeException("La contrasena es obligatoria");
        }
        if (userRequest.getPassword().length() < 8) {
            throw new RuntimeException("La contrasena debe tener al menos 8 caracteres");
        }

        // El rol se determina automaticamente por dominio de email
        // No forzamos roleId para permitir la asignacion automatica
        userRequest.setStatus("Activo");

        return new ResponseEntity<>(userService.createUser(userRequest), HttpStatus.CREATED);
    }

    @PostMapping
    @Operation(summary = "Crear nuevo usuario (Admin puede especificar rol)")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserRequestDTO userRequest) {
        if (userRequest.getEmail() == null || userRequest.getEmail().isBlank()) {
            throw new RuntimeException("El email es obligatorio");
        }
        if (!userRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Formato de email invalido");
        }
        if (userRequest.getNombre() == null || userRequest.getNombre().isBlank()) {
            throw new RuntimeException("El nombre es obligatorio");
        }
        if (userRequest.getApellido() == null || userRequest.getApellido().isBlank()) {
            throw new RuntimeException("El apellido es obligatorio");
        }
        if (userRequest.getPassword() == null || userRequest.getPassword().isEmpty()) {
            throw new RuntimeException("La contrasena es obligatoria al crear un usuario");
        }
        if (userRequest.getPassword().length() < 8) {
            throw new RuntimeException("La contrasena debe tener al menos 8 caracteres");
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
            throw new RuntimeException("Formato de email invalido");
        }
        if (userRequest.getNombre() == null || userRequest.getNombre().isBlank()) {
            throw new RuntimeException("El nombre es obligatorio");
        }
        if (userRequest.getApellido() == null || userRequest.getApellido().isBlank()) {
            throw new RuntimeException("El apellido es obligatorio");
        }
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty() &&
                userRequest.getPassword().length() < 8) {
            throw new RuntimeException("La contrasena debe tener al menos 8 caracteres");
        }
        return ResponseEntity.ok(userService.updateUser(id, userRequest));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Actualizar estado del usuario (Activo/Bloqueado/Suspendido)")
    @PreAuthorize("hasAnyRole('Admin','Soporte')")
    public ResponseEntity<UserDTO> updateUserStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) LocalDateTime suspensionHasta) {
        return ResponseEntity.ok(userService.updateUserStatus(id, status, suspensionHasta));
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "Actualizar rol del usuario (Solo Admin)")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<UserDTO> updateUserRole(
            @PathVariable Long id,
            @RequestParam Long roleId) {
        return ResponseEntity.ok(userService.updateUserRole(id, roleId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/avatar")
    @Operation(summary = "Subir avatar de usuario (multipart/form-data)")
    public ResponseEntity<UserDTO> uploadAvatar(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

            String storedPath = userImageStorageService.storeProfileImage(file, id);
            UserDTO dto = userService.updateUserProfilePic(user.getId(), storedPath);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la imagen", e);
        }
    }

    @GetMapping("/{id}/avatar")
    @Operation(summary = "Obtener avatar del usuario")
    public ResponseEntity<byte[]> getAvatar(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        String storedPath = user.getProfilePic();
        if (storedPath == null || storedPath.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario no tiene avatar");
        }

        Path imagePath = userImageStorageService.resolveProfileImagePath(storedPath);
        if (!Files.exists(imagePath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Imagen no encontrada en el servidor");
        }

        try {
            byte[] bytes = Files.readAllBytes(imagePath);
            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                contentType = "image/jpeg";
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(bytes);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al leer la imagen", e);
        }
    }
}

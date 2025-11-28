package com.fixsy.usuarios.service;

import com.fixsy.usuarios.dto.LoginRequestDTO;
import com.fixsy.usuarios.dto.LoginResponseDTO;
import com.fixsy.usuarios.dto.UserDTO;
import com.fixsy.usuarios.dto.UserRequestDTO;
import com.fixsy.usuarios.model.Role;
import com.fixsy.usuarios.model.User;
import com.fixsy.usuarios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertToDTO(user);
    }

    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertToDTO(user);
    }

    public List<UserDTO> getUsersByRole(String roleName) {
        return userRepository.findByRoleNombre(roleName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getUsersByStatus(String status) {
        return userRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);
        
        if (user == null) {
            return new LoginResponseDTO(false, "Usuario no encontrado", null);
        }
        
        // En producción usar BCrypt para comparar contraseñas hasheadas
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return new LoginResponseDTO(false, "Contraseña incorrecta", null);
        }
        
        if ("Bloqueado".equals(user.getStatus())) {
            return new LoginResponseDTO(false, "Usuario bloqueado", null);
        }
        
        if ("Suspendido".equals(user.getStatus())) {
            if (user.getSuspensionHasta() != null && 
                user.getSuspensionHasta().isAfter(LocalDateTime.now())) {
                return new LoginResponseDTO(false, 
                    "Usuario suspendido hasta " + user.getSuspensionHasta(), null);
            } else {
                // Reactivar usuario si la suspensión expiró
                user.setStatus("Activo");
                user.setSuspensionHasta(null);
                userRepository.save(user);
            }
        }
        
        return new LoginResponseDTO(true, "Login exitoso", convertToDTO(user));
    }

    public UserDTO createUser(UserRequestDTO userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Determinar rol automáticamente por dominio de email
        Role role;
        if (userRequest.getRoleId() != null) {
            // Si se proporciona un roleId, usarlo (para admins creando usuarios)
            role = roleService.getRoleEntityById(userRequest.getRoleId());
        } else {
            // Determinar rol por dominio de email
            role = roleService.determineRoleByEmail(userRequest.getEmail());
        }

        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword()); // En producción, hashear con BCrypt
        user.setNombre(userRequest.getNombre());
        user.setApellido(userRequest.getApellido());
        user.setPhone(userRequest.getPhone());
        user.setRole(role);
        user.setStatus(userRequest.getStatus() != null ? userRequest.getStatus() : "Activo");
        user.setProfilePic(userRequest.getProfilePic());
        user.setSuspensionHasta(userRequest.getSuspensionHasta());

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO updateUser(Long id, UserRequestDTO userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar email único si cambió
        if (!user.getEmail().equalsIgnoreCase(userRequest.getEmail()) &&
            userRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("El email ya está en uso por otro usuario");
        }

        // Si el email cambia, re-evaluar el rol
        if (!user.getEmail().equalsIgnoreCase(userRequest.getEmail())) {
            Role newRole = roleService.determineRoleByEmail(userRequest.getEmail());
            user.setRole(newRole);
        } else if (userRequest.getRoleId() != null) {
            // Si se proporciona roleId explícito, usarlo
            Role role = roleService.getRoleEntityById(userRequest.getRoleId());
            user.setRole(role);
        }

        user.setEmail(userRequest.getEmail());
        user.setNombre(userRequest.getNombre());
        user.setApellido(userRequest.getApellido());
        user.setPhone(userRequest.getPhone());
        
        // Solo actualizar contraseña si se proporciona
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty() && 
            userRequest.getPassword().length() >= 8) {
            user.setPassword(userRequest.getPassword()); // En producción, hashear
        }

        if (userRequest.getStatus() != null) {
            user.setStatus(userRequest.getStatus());
        }
        if (userRequest.getProfilePic() != null) {
            user.setProfilePic(userRequest.getProfilePic());
        }
        user.setSuspensionHasta(userRequest.getSuspensionHasta());

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    public UserDTO updateUserStatus(Long id, String status, LocalDateTime suspensionHasta) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        user.setStatus(status);
        if ("Suspendido".equals(status)) {
            user.setSuspensionHasta(suspensionHasta);
        } else {
            user.setSuspensionHasta(null);
        }
        
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    public UserDTO updateUserRole(Long id, Long roleId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Role role = roleService.getRoleEntityById(roleId);
        user.setRole(role);
        
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getNombre(),
                user.getApellido(),
                user.getPhone(),
                roleService.convertToDTO(user.getRole()),
                user.getStatus(),
                user.getProfilePic(),
                user.getSuspensionHasta(),
                user.getCreatedAt()
        );
    }
}

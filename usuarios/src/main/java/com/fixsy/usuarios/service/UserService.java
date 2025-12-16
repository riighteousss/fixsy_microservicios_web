package com.fixsy.usuarios.service;

import com.fixsy.usuarios.dto.LoginRequestDTO;
import com.fixsy.usuarios.dto.LoginResponseDTO;
import com.fixsy.usuarios.dto.RoleSummaryDTO;
import com.fixsy.usuarios.dto.UserDTO;
import com.fixsy.usuarios.dto.UserRequestDTO;
import com.fixsy.usuarios.dto.RegisterDTO;
import com.fixsy.usuarios.model.Role;
import com.fixsy.usuarios.model.User;
import com.fixsy.usuarios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    public User loginDirect(String email, String password) {
        if (email == null || password == null)
            throw new RuntimeException("Email y password son requeridos");
        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Soporta tanto bcrypt como texto plano (para legado/desarrollo)
        boolean matches = false;
        if (user.getPassword() != null && user.getPassword().startsWith("$2")) {
            matches = passwordEncoder.matches(password, user.getPassword());
        } else {
            matches = user.getPassword() != null && user.getPassword().equals(password);
            // Actualizar a bcrypt si coincidió en texto plano
            if (matches) {
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
            }
        }

        if (!matches) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return user;
    }

    public User registerUserDirect(RegisterDTO dto) {
        String email = dto.getEmail().toLowerCase().trim();

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("El email ya está registrado");
        }

        // REGLA DE ASIGNACIÓN DE ROLES (Delegada a RoleService)
        Role role = roleService.determineRoleByEmail(email);

        User user = new User();
        user.setEmail(email);
        user.setNombre(dto.getNombres());
        user.setApellido(dto.getApellidos());
        user.setPhone(normalizePhone(dto.getTelefono()));
        user.setPassword(passwordEncoder.encode(dto.getContrasena()));
        user.setRole(role);
        user.setStatus("Activo");

        return userRepository.save(user);
    }

    // Login heredado obsoleto - manteniendo firma para evitar romper UserController
    // pero no usado por AuthController
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            User user = loginDirect(loginRequest.getEmail(), loginRequest.getPassword());
            return new LoginResponseDTO(true, "Login exitoso", convertToDTO(user), null);
        } catch (Exception e) {
            return new LoginResponseDTO(false, e.getMessage(), null, null);
        }
    }

    public UserDTO createUser(UserRequestDTO userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("El email ya esta registrado");
        }

        Role role = roleService.determineRoleByEmail(userRequest.getEmail());
        // SE FUERZA LA LOGICA DE DOMINIO - Se ignora roleId del body para seguridad

        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setNombre(userRequest.getNombre());
        user.setApellido(userRequest.getApellido());
        user.setPhone(normalizePhone(userRequest.getPhone()));
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

        if (!user.getEmail().equalsIgnoreCase(userRequest.getEmail()) &&
                userRepository.existsByEmail(userRequest.getEmail())) {
            throw new RuntimeException("El email ya esta en uso por otro usuario");
        }

        if (!user.getEmail().equalsIgnoreCase(userRequest.getEmail())) {
            Role newRole = roleService.determineRoleByEmail(userRequest.getEmail());
            user.setRole(newRole);
        } else if (userRequest.getRoleId() != null) {
            Role role = roleService.getRoleEntityById(userRequest.getRoleId());
            user.setRole(role);
        }

        user.setEmail(userRequest.getEmail());
        user.setNombre(userRequest.getNombre());
        user.setApellido(userRequest.getApellido());
        user.setPhone(normalizePhone(userRequest.getPhone()));

        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty() &&
                userRequest.getPassword().length() >= 8) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
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

    public UserDTO updateUserProfilePic(Long id, String profilePicPath) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setProfilePic(profilePicPath);
        User updated = userRepository.save(user);
        return convertToDTO(updated);
    }

    private String normalizePhone(String phone) {
        return phone == null ? "" : phone.trim();
    }

    private UserDTO convertToDTO(User user) {
        Role role = user.getRole();
        RoleSummaryDTO roleSummary = null;
        Long roleId = null;
        if (role != null) {
            roleId = role.getId();
            roleSummary = new RoleSummaryDTO(
                    role.getId(),
                    role.getNombre(),
                    role.getDescripcion());
        }

        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getNombre(),
                user.getApellido(),
                roleId,
                user.getPhone(),
                roleSummary,
                user.getStatus(),
                user.getProfilePic(),
                user.getSuspensionHasta(),
                user.getCreatedAt());
    }
}

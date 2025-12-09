package com.fixsy.usuarios.config;

import com.fixsy.usuarios.model.Role;
import com.fixsy.usuarios.model.User;
import com.fixsy.usuarios.repository.RoleRepository;
import com.fixsy.usuarios.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seed de datos basicos para desarrollo/demos.
 * Crea roles y usuarios de prueba solo cuando hay pocos registros.
 */
@Component
@RequiredArgsConstructor
public class UsuariosDataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedRoles();
        seedUsers();
    }

    private void seedRoles() {
        // Roles principales se crean en RoleService.initializeRoles()
    }

    private void seedUsers() {
        if (userRepository.count() >= 5) {
            return;
        }
        Role usuarioRole = roleRepository.findByNombre("Usuario").orElse(null);
        Role clienteRole = roleRepository.findByNombre("Cliente").orElse(usuarioRole);
        Role adminRole = roleRepository.findByNombre("Admin").orElse(null);

        createUserIfMissing("cliente@cliente.fixsy.com", "Cliente123", "Carla", "Cliente", "+56911112222", clienteRole);
        createUserIfMissing("admin@admin.fixsy.com", "Admin123", "Ana", "Admin", "+56922223333", adminRole);
        createUserIfMissing("usuario@fixsy.com", "Usuario123", "Ursula", "Usuario", "+56955556666", usuarioRole);
    }

    private void createUserIfMissing(String email,
                                     String password,
                                     String nombre,
                                     String apellido,
                                     String phone,
                                     Role role) {
        if (email == null || role == null) {
            return;
        }
        if (userRepository.existsByEmail(email)) {
            return;
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setNombre(nombre);
        user.setApellido(apellido);
        user.setPhone(phone);
        user.setRole(role);
        user.setStatus("Activo");
        userRepository.save(user);
    }
}

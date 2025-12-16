package com.fixsy.usuarios.config;

import com.fixsy.usuarios.model.Role;
import com.fixsy.usuarios.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1) // Run before other runners if any
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- Ejecutando RoleInitializer ---");

        createRoleIfNotFound("Admin", "Administrador con acceso completo al sistema", "adminfixsy.cl");
        createRoleIfNotFound("Soporte", "Personal de soporte tecnico", "fixsy.cl");
        createRoleIfNotFound("Usuario", "Cliente normal de la tienda", null); // Default role
        // Optional: Ensure "Cliente" exists if it was used before, pointing to same
        // concept as Usuario
        createRoleIfNotFound("Cliente", "Cliente final de la tienda (Legacy)", null);

        System.out.println("--- Roles Verificados/Inicializados ---");
    }

    private void createRoleIfNotFound(String nombre, String description, String emailDomain) {
        if (!roleRepository.existsByNombre(nombre)) {
            Role role = new Role(nombre, description, emailDomain);
            roleRepository.save(role);
            System.out.println("Rol creado: " + nombre);
        } else {
            // Optional: Update domain if needed, but for now we just ensure existence.
            // If we wanted to enforce domains:
            roleRepository.findByNombre(nombre).ifPresent(role -> {
                if (emailDomain != null && !emailDomain.equals(role.getEmailDomain())) {
                    role.setEmailDomain(emailDomain);
                    roleRepository.save(role);
                    System.out.println("Rol actualizado (dominio): " + nombre);
                }
            });
        }
    }
}

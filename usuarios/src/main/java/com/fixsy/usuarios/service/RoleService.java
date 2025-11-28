package com.fixsy.usuarios.service;

import com.fixsy.usuarios.dto.RoleDTO;
import com.fixsy.usuarios.model.Role;
import com.fixsy.usuarios.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Inicializa los roles por defecto al arrancar la aplicación
     */
    @PostConstruct
    public void initializeRoles() {
        // Crear rol Usuario si no existe
        if (!roleRepository.existsByNombre("Usuario")) {
            Role usuarioRole = new Role(
                "Usuario", 
                "Cliente normal de la tienda", 
                null // Sin dominio específico - es el rol por defecto
            );
            roleRepository.save(usuarioRole);
        }

        // Crear rol Admin si no existe
        if (!roleRepository.existsByNombre("Admin")) {
            Role adminRole = new Role(
                "Admin", 
                "Administrador con acceso completo al sistema", 
                "admin.fixsy.com"
            );
            roleRepository.save(adminRole);
        }

        // Crear rol Soporte si no existe
        if (!roleRepository.existsByNombre("Soporte")) {
            Role soporteRole = new Role(
                "Soporte", 
                "Personal de soporte al cliente", 
                "soporte.fixsy.com"
            );
            roleRepository.save(soporteRole);
        }
    }

    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RoleDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        return convertToDTO(role);
    }

    public RoleDTO getRoleByNombre(String nombre) {
        Role role = roleRepository.findByNombre(nombre)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        return convertToDTO(role);
    }

    public Role getRoleEntityByNombre(String nombre) {
        return roleRepository.findByNombre(nombre)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombre));
    }

    public Role getRoleEntityById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
    }

    /**
     * Determina el rol basándose en el dominio del email
     * - @admin.fixsy.com -> Admin
     * - @soporte.fixsy.com -> Soporte
     * - Cualquier otro dominio -> Usuario
     */
    public Role determineRoleByEmail(String email) {
        String domain = email.toLowerCase().split("@")[1];
        
        // Buscar si hay un rol con este dominio
        return roleRepository.findByEmailDomain(domain)
                .orElseGet(() -> getRoleEntityByNombre("Usuario"));
    }

    public RoleDTO convertToDTO(Role role) {
        return new RoleDTO(
                role.getId(),
                role.getNombre(),
                role.getDescripcion(),
                role.getEmailDomain()
        );
    }
}


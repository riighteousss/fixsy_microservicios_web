package com.fixsy.usuarios.service;

import com.fixsy.usuarios.dto.RoleDTO;
import com.fixsy.usuarios.model.Role;
import com.fixsy.usuarios.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Inicializa roles base al arrancar la aplicación.
     */
    @PostConstruct
    public void initializeRoles() {
        createIfMissing("Admin", "Administrador con acceso completo al sistema", "admin.fixsy.com");
        createIfMissing("Cliente", "Cliente final de la tienda", null);
        createIfMissing("Usuario", "Cliente normal de la tienda (compatibilidad)", null);
    }

    private void createIfMissing(String nombre, String descripcion, String dominio) {
        if (!roleRepository.existsByNombre(nombre)) {
            roleRepository.save(new Role(nombre, descripcion, dominio));
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
     * Determina el rol basándose en el dominio del email con validación estricta.
     * - @adminfixsy.cl -> Admin
     * - @fixsy.cl -> Soporte
     * - Cualquier otro -> Usuario
     */
    public Role determineRoleByEmail(String email) {
        if (email == null)
            return getRoleEntityByNombre("Usuario");

        String lowerEmail = email.toLowerCase().trim();

        if (lowerEmail.endsWith("@adminfixsy.cl")) {
            return roleRepository.findByNombre("Admin")
                    .orElseThrow(() -> new RuntimeException("Rol Admin no encontrado"));
        }

        if (lowerEmail.endsWith("@fixsy.cl")) {
            return roleRepository.findByNombre("Soporte")
                    .orElseThrow(() -> new RuntimeException("Rol Soporte no encontrado"));
        }

        return getRoleEntityByNombre("Usuario");
    }

    public RoleDTO convertToDTO(Role role) {
        return new RoleDTO(
                role.getId(),
                role.getNombre(),
                role.getDescripcion(),
                role.getEmailDomain());
    }

}

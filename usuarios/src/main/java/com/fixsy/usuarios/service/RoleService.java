package com.fixsy.usuarios.service;

import com.fixsy.usuarios.dto.RoleDTO;
import com.fixsy.usuarios.model.Role;
import com.fixsy.usuarios.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

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
     * - @fixsy.cl / @fixsy.com -> Soporte
     * - Cualquier otro -> Usuario
     */
    public Role determineRoleByEmail(String email) {
        if (email == null) {
            return getRoleEntityByNombre("Usuario");
        }

        // 1. Sanitización
        String emailSanitized = email.trim().toLowerCase();

        // 2. Lógica Blindada
        String finalRoleName;
        if (emailSanitized.endsWith("@adminfixsy.cl")) {
            finalRoleName = "Admin";
        } else if (emailSanitized.endsWith("@fixsy.cl") || emailSanitized.endsWith("@fixsy.com")) {
            finalRoleName = "Soporte";
        } else {
            finalRoleName = "Usuario";
        }

        // 3. Obtención de Entidad
        return roleRepository.findByNombre(finalRoleName)
                .orElseThrow(() -> new RuntimeException("Rol crítico no encontrado en BD: " + finalRoleName));
    }

    public RoleDTO convertToDTO(Role role) {
        return new RoleDTO(
                role.getId(),
                role.getNombre(),
                role.getDescripcion(),
                role.getEmailDomain());
    }

}

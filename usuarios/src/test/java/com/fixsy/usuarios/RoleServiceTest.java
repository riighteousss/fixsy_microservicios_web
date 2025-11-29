package com.fixsy.usuarios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fixsy.usuarios.dto.RoleDTO;
import com.fixsy.usuarios.model.Role;
import com.fixsy.usuarios.repository.RoleRepository;
import com.fixsy.usuarios.service.RoleService;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role userRole;
    private Role adminRole;
    private Role soporteRole;

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setId(1L);
        userRole.setNombre("Usuario");
        userRole.setDescripcion("Cliente normal de la tienda");
        userRole.setEmailDomain(null);

        adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setNombre("Admin");
        adminRole.setDescripcion("Administrador con acceso completo al sistema");
        adminRole.setEmailDomain("admin.fixsy.com");

        soporteRole = new Role();
        soporteRole.setId(3L);
        soporteRole.setNombre("Soporte");
        soporteRole.setDescripcion("Personal de soporte al cliente");
        soporteRole.setEmailDomain("soporte.fixsy.com");
    }

    @Test
    void testGetAllRoles() {
        when(roleRepository.findAll()).thenReturn(Arrays.asList(userRole, adminRole, soporteRole));

        List<RoleDTO> result = roleService.getAllRoles();

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(roleRepository).findAll();
    }

    @Test
    void testGetRoleById() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(userRole));

        RoleDTO result = roleService.getRoleById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Usuario", result.getNombre());
    }

    @Test
    void testGetRoleByIdNotFound() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> roleService.getRoleById(99L));
    }

    @Test
    void testGetRoleByNombre() {
        when(roleRepository.findByNombre("Admin")).thenReturn(Optional.of(adminRole));

        RoleDTO result = roleService.getRoleByNombre("Admin");

        assertNotNull(result);
        assertEquals("Admin", result.getNombre());
        assertEquals("admin.fixsy.com", result.getEmailDomain());
    }

    @Test
    void testGetRoleByNombreNotFound() {
        when(roleRepository.findByNombre("Inexistente")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> roleService.getRoleByNombre("Inexistente"));
    }

    @Test
    void testGetRoleEntityByNombre() {
        when(roleRepository.findByNombre("Usuario")).thenReturn(Optional.of(userRole));

        Role result = roleService.getRoleEntityByNombre("Usuario");

        assertNotNull(result);
        assertEquals("Usuario", result.getNombre());
    }

    @Test
    void testGetRoleEntityByNombreNotFound() {
        when(roleRepository.findByNombre("Inexistente")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> roleService.getRoleEntityByNombre("Inexistente"));
        assertTrue(exception.getMessage().contains("Rol no encontrado"));
    }

    @Test
    void testGetRoleEntityById() {
        when(roleRepository.findById(2L)).thenReturn(Optional.of(adminRole));

        Role result = roleService.getRoleEntityById(2L);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Admin", result.getNombre());
    }

    @Test
    void testGetRoleEntityByIdNotFound() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> roleService.getRoleEntityById(99L));
    }

    @Test
    void testDetermineRoleByEmailAdmin() {
        when(roleRepository.findByEmailDomain("admin.fixsy.com")).thenReturn(Optional.of(adminRole));

        Role result = roleService.determineRoleByEmail("usuario@admin.fixsy.com");

        assertNotNull(result);
        assertEquals("Admin", result.getNombre());
    }

    @Test
    void testDetermineRoleByEmailSoporte() {
        when(roleRepository.findByEmailDomain("soporte.fixsy.com")).thenReturn(Optional.of(soporteRole));

        Role result = roleService.determineRoleByEmail("agente@soporte.fixsy.com");

        assertNotNull(result);
        assertEquals("Soporte", result.getNombre());
    }

    @Test
    void testDetermineRoleByEmailDefaultUser() {
        when(roleRepository.findByEmailDomain("gmail.com")).thenReturn(Optional.empty());
        when(roleRepository.findByNombre("Usuario")).thenReturn(Optional.of(userRole));

        Role result = roleService.determineRoleByEmail("cliente@gmail.com");

        assertNotNull(result);
        assertEquals("Usuario", result.getNombre());
    }

    @Test
    void testConvertToDTO() {
        RoleDTO result = roleService.convertToDTO(adminRole);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Admin", result.getNombre());
        assertEquals("Administrador con acceso completo al sistema", result.getDescripcion());
        assertEquals("admin.fixsy.com", result.getEmailDomain());
    }

    @Test
    void testInitializeRolesCreatesAll() {
        when(roleRepository.existsByNombre("Usuario")).thenReturn(false);
        when(roleRepository.existsByNombre("Admin")).thenReturn(false);
        when(roleRepository.existsByNombre("Soporte")).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenAnswer(i -> i.getArgument(0));

        roleService.initializeRoles();

        verify(roleRepository, times(3)).save(any(Role.class));
    }

    @Test
    void testInitializeRolesAlreadyExist() {
        when(roleRepository.existsByNombre("Usuario")).thenReturn(true);
        when(roleRepository.existsByNombre("Admin")).thenReturn(true);
        when(roleRepository.existsByNombre("Soporte")).thenReturn(true);

        roleService.initializeRoles();

        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testInitializeRolesPartiallyExist() {
        when(roleRepository.existsByNombre("Usuario")).thenReturn(true);
        when(roleRepository.existsByNombre("Admin")).thenReturn(false);
        when(roleRepository.existsByNombre("Soporte")).thenReturn(true);
        when(roleRepository.save(any(Role.class))).thenAnswer(i -> i.getArgument(0));

        roleService.initializeRoles();

        verify(roleRepository, times(1)).save(any(Role.class));
    }
}


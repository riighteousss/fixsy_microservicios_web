package com.fixsy.usuarios;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fixsy.usuarios.controller.RoleController;
import com.fixsy.usuarios.dto.RoleDTO;
import com.fixsy.usuarios.service.RoleService;

@WebMvcTest(RoleController.class)
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    private RoleDTO userRoleDTO;
    private RoleDTO adminRoleDTO;
    private RoleDTO soporteRoleDTO;

    @BeforeEach
    void setUp() {
        userRoleDTO = new RoleDTO(1L, "Usuario", "Cliente normal de la tienda", null);
        adminRoleDTO = new RoleDTO(2L, "Admin", "Administrador con acceso completo al sistema", "admin.fixsy.com");
        soporteRoleDTO = new RoleDTO(3L, "Soporte", "Personal de soporte al cliente", "soporte.fixsy.com");
    }

    @Test
    void testGetAllRoles() throws Exception {
        when(roleService.getAllRoles()).thenReturn(Arrays.asList(userRoleDTO, adminRoleDTO, soporteRoleDTO));

        mockMvc.perform(get("/api/roles")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].nombre").value("Usuario"))
            .andExpect(jsonPath("$[1].nombre").value("Admin"))
            .andExpect(jsonPath("$[2].nombre").value("Soporte"));
    }

    @Test
    void testGetRoleById() throws Exception {
        when(roleService.getRoleById(1L)).thenReturn(userRoleDTO);

        mockMvc.perform(get("/api/roles/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.nombre").value("Usuario"))
            .andExpect(jsonPath("$.descripcion").value("Cliente normal de la tienda"));
    }

    @Test
    void testGetRoleByIdAdmin() throws Exception {
        when(roleService.getRoleById(2L)).thenReturn(adminRoleDTO);

        mockMvc.perform(get("/api/roles/2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.nombre").value("Admin"))
            .andExpect(jsonPath("$.emailDomain").value("admin.fixsy.com"));
    }

    @Test
    void testGetRoleByIdNotFound() throws Exception {
        when(roleService.getRoleById(99L)).thenThrow(new RuntimeException("Rol no encontrado"));

        mockMvc.perform(get("/api/roles/99")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetRoleByNombre() throws Exception {
        when(roleService.getRoleByNombre("Usuario")).thenReturn(userRoleDTO);

        mockMvc.perform(get("/api/roles/nombre/Usuario")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Usuario"))
            .andExpect(jsonPath("$.descripcion").value("Cliente normal de la tienda"));
    }

    @Test
    void testGetRoleByNombreAdmin() throws Exception {
        when(roleService.getRoleByNombre("Admin")).thenReturn(adminRoleDTO);

        mockMvc.perform(get("/api/roles/nombre/Admin")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Admin"))
            .andExpect(jsonPath("$.descripcion").value("Administrador con acceso completo al sistema"))
            .andExpect(jsonPath("$.emailDomain").value("admin.fixsy.com"));
    }

    @Test
    void testGetRoleByNombreSoporte() throws Exception {
        when(roleService.getRoleByNombre("Soporte")).thenReturn(soporteRoleDTO);

        mockMvc.perform(get("/api/roles/nombre/Soporte")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Soporte"))
            .andExpect(jsonPath("$.emailDomain").value("soporte.fixsy.com"));
    }

    @Test
    void testGetRoleByNombreNotFound() throws Exception {
        when(roleService.getRoleByNombre("Inexistente")).thenThrow(new RuntimeException("Rol no encontrado"));

        mockMvc.perform(get("/api/roles/nombre/Inexistente")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());
    }
}


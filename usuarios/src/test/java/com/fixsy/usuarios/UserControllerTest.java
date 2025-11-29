package com.fixsy.usuarios;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fixsy.usuarios.controller.UserController;
import com.fixsy.usuarios.dto.LoginRequestDTO;
import com.fixsy.usuarios.dto.LoginResponseDTO;
import com.fixsy.usuarios.dto.RoleDTO;
import com.fixsy.usuarios.dto.UserDTO;
import com.fixsy.usuarios.dto.UserRequestDTO;
import com.fixsy.usuarios.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private ObjectMapper objectMapper;
    private UserDTO testUserDTO;
    private RoleDTO testRoleDTO;
    private UserRequestDTO testUserRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testRoleDTO = new RoleDTO(1L, "Usuario", "Cliente normal de la tienda", null);

        testUserDTO = new UserDTO(
            1L,
            "test@example.com",
            "Juan",
            "Pérez",
            "+56912345678",
            testRoleDTO,
            "Activo",
            null,
            null,
            LocalDateTime.now()
        );

        testUserRequest = new UserRequestDTO();
        testUserRequest.setEmail("test@example.com");
        testUserRequest.setPassword("password123");
        testUserRequest.setNombre("Juan");
        testUserRequest.setApellido("Pérez");
        testUserRequest.setPhone("+56912345678");
        testUserRequest.setStatus("Activo");
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUserDTO));

        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].email").value("test@example.com"))
            .andExpect(jsonPath("$[0].nombre").value("Juan"));
    }

    @Test
    void testGetAllUsersEmpty() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetUserById() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUserDTO);

        mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.nombre").value("Juan"))
            .andExpect(jsonPath("$.apellido").value("Pérez"));
    }

    @Test
    void testGetUserByEmail() throws Exception {
        when(userService.getUserByEmail("test@example.com")).thenReturn(testUserDTO);

        mockMvc.perform(get("/api/users/email/test@example.com")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testGetUsersByRole() throws Exception {
        when(userService.getUsersByRole("Usuario")).thenReturn(Arrays.asList(testUserDTO));

        mockMvc.perform(get("/api/users/role/Usuario")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].role.nombre").value("Usuario"));
    }

    @Test
    void testGetUsersByStatus() throws Exception {
        when(userService.getUsersByStatus("Activo")).thenReturn(Arrays.asList(testUserDTO));

        mockMvc.perform(get("/api/users/status/Activo")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].status").value("Activo"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "password123");
        LoginResponseDTO loginResponse = new LoginResponseDTO(true, "Login exitoso", testUserDTO);

        when(userService.login(Mockito.any(LoginRequestDTO.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Login exitoso"))
            .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    void testLoginFailed() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "wrongpassword");
        LoginResponseDTO loginResponse = new LoginResponseDTO(false, "Contraseña incorrecta", null);

        when(userService.login(Mockito.any(LoginRequestDTO.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Contraseña incorrecta"));
    }

    @Test
    void testRegisterUser() throws Exception {
        when(userService.createUser(Mockito.any(UserRequestDTO.class))).thenReturn(testUserDTO);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    void testRegisterUserMissingEmail() throws Exception {
        testUserRequest.setEmail(null);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserRequest)))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testRegisterUserInvalidEmail() throws Exception {
        testUserRequest.setEmail("invalid-email");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserRequest)))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testRegisterUserShortPassword() throws Exception {
        testUserRequest.setPassword("1234567");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserRequest)))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testCreateUser() throws Exception {
        when(userService.createUser(Mockito.any(UserRequestDTO.class))).thenReturn(testUserDTO);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testUpdateUser() throws Exception {
        when(userService.updateUser(eq(1L), Mockito.any(UserRequestDTO.class))).thenReturn(testUserDTO);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testUpdateUserStatus() throws Exception {
        when(userService.updateUserStatus(eq(1L), eq("Bloqueado"), isNull())).thenReturn(testUserDTO);

        mockMvc.perform(put("/api/users/1/status")
                .param("status", "Bloqueado")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void testUpdateUserRole() throws Exception {
        when(userService.updateUserRole(1L, 2L)).thenReturn(testUserDTO);

        mockMvc.perform(put("/api/users/1/role")
                .param("roleId", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }
}


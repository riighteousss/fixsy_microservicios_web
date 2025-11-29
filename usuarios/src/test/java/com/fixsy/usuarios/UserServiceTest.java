package com.fixsy.usuarios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fixsy.usuarios.dto.LoginRequestDTO;
import com.fixsy.usuarios.dto.LoginResponseDTO;
import com.fixsy.usuarios.dto.RoleDTO;
import com.fixsy.usuarios.dto.UserDTO;
import com.fixsy.usuarios.dto.UserRequestDTO;
import com.fixsy.usuarios.model.Role;
import com.fixsy.usuarios.model.User;
import com.fixsy.usuarios.repository.UserRepository;
import com.fixsy.usuarios.service.RoleService;
import com.fixsy.usuarios.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role testRole;
    private RoleDTO testRoleDTO;
    private UserRequestDTO testUserRequest;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1L);
        testRole.setNombre("Usuario");
        testRole.setDescripcion("Cliente normal de la tienda");
        testRole.setEmailDomain(null);

        testRoleDTO = new RoleDTO(1L, "Usuario", "Cliente normal de la tienda", null);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setNombre("Juan");
        testUser.setApellido("Pérez");
        testUser.setPhone("+56912345678");
        testUser.setRole(testRole);
        testUser.setStatus("Activo");
        testUser.setCreatedAt(LocalDateTime.now());

        testUserRequest = new UserRequestDTO();
        testUserRequest.setEmail("test@example.com");
        testUserRequest.setPassword("password123");
        testUserRequest.setNombre("Juan");
        testUserRequest.setApellido("Pérez");
        testUserRequest.setPhone("+56912345678");
        testUserRequest.setStatus("Activo");
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));
        when(roleService.convertToDTO(any(Role.class))).thenReturn(testRoleDTO);

        List<UserDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test@example.com", result.get(0).getEmail());
        verify(userRepository).findAll();
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleService.convertToDTO(any(Role.class))).thenReturn(testRoleDTO);

        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(99L));
    }

    @Test
    void testGetUserByEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(roleService.convertToDTO(any(Role.class))).thenReturn(testRoleDTO);

        UserDTO result = userService.getUserByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void testGetUserByEmailNotFound() {
        when(userRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserByEmail("noexiste@example.com"));
    }

    @Test
    void testGetUsersByRole() {
        when(userRepository.findByRoleNombre("Usuario")).thenReturn(Arrays.asList(testUser));
        when(roleService.convertToDTO(any(Role.class))).thenReturn(testRoleDTO);

        List<UserDTO> result = userService.getUsersByRole("Usuario");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetUsersByStatus() {
        when(userRepository.findByStatus("Activo")).thenReturn(Arrays.asList(testUser));
        when(roleService.convertToDTO(any(Role.class))).thenReturn(testRoleDTO);

        List<UserDTO> result = userService.getUsersByStatus("Activo");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testLoginSuccess() {
        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "password123");
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(roleService.convertToDTO(any(Role.class))).thenReturn(testRoleDTO);

        LoginResponseDTO result = userService.login(loginRequest);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Login exitoso", result.getMessage());
        assertNotNull(result.getUser());
    }

    @Test
    void testLoginUserNotFound() {
        LoginRequestDTO loginRequest = new LoginRequestDTO("noexiste@example.com", "password123");
        
        when(userRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        LoginResponseDTO result = userService.login(loginRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Usuario no encontrado", result.getMessage());
    }

    @Test
    void testLoginWrongPassword() {
        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "wrongpassword");
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        LoginResponseDTO result = userService.login(loginRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Contraseña incorrecta", result.getMessage());
    }

    @Test
    void testLoginBlockedUser() {
        testUser.setStatus("Bloqueado");
        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "password123");
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        LoginResponseDTO result = userService.login(loginRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Usuario bloqueado", result.getMessage());
    }

    @Test
    void testLoginSuspendedUser() {
        testUser.setStatus("Suspendido");
        testUser.setSuspensionHasta(LocalDateTime.now().plusDays(1));
        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "password123");
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        LoginResponseDTO result = userService.login(loginRequest);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Usuario suspendido hasta"));
    }

    @Test
    void testLoginExpiredSuspension() {
        testUser.setStatus("Suspendido");
        testUser.setSuspensionHasta(LocalDateTime.now().minusDays(1));
        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "password123");
        
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(roleService.convertToDTO(any(Role.class))).thenReturn(testRoleDTO);

        LoginResponseDTO result = userService.login(loginRequest);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUserSuccess() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(roleService.determineRoleByEmail("test@example.com")).thenReturn(testRole);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(roleService.convertToDTO(any(Role.class))).thenReturn(testRoleDTO);

        UserDTO result = userService.createUser(testUserRequest);

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUserWithRoleId() {
        testUserRequest.setRoleId(1L);
        
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(roleService.getRoleEntityById(1L)).thenReturn(testRole);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(roleService.convertToDTO(any(Role.class))).thenReturn(testRoleDTO);

        UserDTO result = userService.createUser(testUserRequest);

        assertNotNull(result);
        verify(roleService).getRoleEntityById(1L);
    }

    @Test
    void testCreateUserEmailExists() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.createUser(testUserRequest));
    }

    @Test
    void testUpdateUserSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(roleService.convertToDTO(any(Role.class))).thenReturn(testRoleDTO);

        UserDTO result = userService.updateUser(1L, testUserRequest);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.updateUser(99L, testUserRequest));
    }

    @Test
    void testUpdateUserEmailAlreadyInUse() {
        testUserRequest.setEmail("otro@example.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("otro@example.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.updateUser(1L, testUserRequest));
    }

    @Test
    void testUpdateUserStatus() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(roleService.convertToDTO(any(Role.class))).thenReturn(testRoleDTO);

        UserDTO result = userService.updateUserStatus(1L, "Bloqueado", null);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUserStatusSuspended() {
        LocalDateTime suspensionDate = LocalDateTime.now().plusDays(7);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(roleService.convertToDTO(any(Role.class))).thenReturn(testRoleDTO);

        UserDTO result = userService.updateUserStatus(1L, "Suspendido", suspensionDate);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUserRole() {
        Role adminRole = new Role();
        adminRole.setId(2L);
        adminRole.setNombre("Admin");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleService.getRoleEntityById(2L)).thenReturn(adminRole);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(roleService.convertToDTO(any(Role.class))).thenReturn(testRoleDTO);

        UserDTO result = userService.updateUserRole(1L, 2L);

        assertNotNull(result);
        verify(roleService).getRoleEntityById(2L);
    }

    @Test
    void testDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.deleteUser(99L));
    }
}


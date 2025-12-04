package com.fixsy.usuarios.controller;

import com.fixsy.usuarios.model.User;
import com.fixsy.usuarios.repository.UserRepository;
import com.fixsy.usuarios.service.UserImageStorageService;
import com.fixsy.usuarios.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@SuppressWarnings("removal")
class UserControllerAvatarTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserImageStorageService userImageStorageService;

    @Test
    void uploadAvatar_shouldUpdateProfilePic() throws Exception {
        User user = buildUser(1L);
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", "img".getBytes());

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userImageStorageService.storeProfileImage(any(), eq(1L))).willReturn("user_1_abc.png");
        given(userService.updateUserProfilePic(eq(1L), eq("user_1_abc.png"))).willReturn(null);

        mockMvc.perform(multipart("/api/users/1/avatar").file(file))
                .andExpect(status().isOk());

        verify(userService).updateUserProfilePic(1L, "user_1_abc.png");
    }

    @Test
    void uploadAvatar_shouldReturn404WhenUserNotFound() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", "img".getBytes());
        given(userRepository.findById(99L)).willReturn(Optional.empty());

        mockMvc.perform(multipart("/api/users/99/avatar").file(file))
                .andExpect(status().isNotFound());
    }

    @Test
    void uploadAvatar_shouldReturn400WhenFileEmpty() throws Exception {
        User user = buildUser(2L);
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", new byte[0]);
        given(userRepository.findById(2L)).willReturn(Optional.of(user));
        given(userImageStorageService.storeProfileImage(any(), eq(2L)))
                .willThrow(new IllegalArgumentException("El archivo está vacío"));

        mockMvc.perform(multipart("/api/users/2/avatar").file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadAvatar_shouldReturn400WhenNotImage() throws Exception {
        User user = buildUser(3L);
        MockMultipartFile file = new MockMultipartFile("file", "avatar.txt", "text/plain", "nope".getBytes());
        given(userRepository.findById(3L)).willReturn(Optional.of(user));
        given(userImageStorageService.storeProfileImage(any(), eq(3L)))
                .willThrow(new IllegalArgumentException("Solo se permiten archivos de imagen"));

        mockMvc.perform(multipart("/api/users/3/avatar").file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAvatar_shouldReturnImageBytes() throws Exception {
        User user = buildUser(4L);
        user.setProfilePic("user_4_test.png");
        Path temp = Files.createTempFile("user_4_test", ".png");
        Files.writeString(temp, "imgdata");

        given(userRepository.findById(4L)).willReturn(Optional.of(user));
        given(userImageStorageService.resolveProfileImagePath("user_4_test.png")).willReturn(temp);

        mockMvc.perform(get("/api/users/4/avatar"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"));
    }

    @Test
    void getAvatar_shouldReturn404WhenUserWithoutAvatar() throws Exception {
        User user = buildUser(5L);
        user.setProfilePic(null);
        given(userRepository.findById(5L)).willReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/5/avatar"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAvatar_shouldReturn404WhenUserNotFound() throws Exception {
        given(userRepository.findById(123L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/users/123/avatar"))
                .andExpect(status().isNotFound());
    }

    private User buildUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("user" + id + "@example.com");
        user.setPassword("password123");
        user.setNombre("Nombre");
        user.setApellido("Apellido");
        user.setStatus("Activo");
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }
}

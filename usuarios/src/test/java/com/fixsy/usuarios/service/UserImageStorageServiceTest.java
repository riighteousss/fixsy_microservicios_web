package com.fixsy.usuarios.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class UserImageStorageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void storeProfileImage_shouldSaveValidImage() throws IOException {
        UserImageStorageService service = new UserImageStorageService(tempDir.toString());
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", "data".getBytes());

        String stored = service.storeProfileImage(file, 1L);

        Path storedPath = tempDir.resolve(stored);
        assertTrue(Files.exists(storedPath));
        assertTrue(stored.startsWith("user_1_"));
        assertTrue(stored.endsWith(".png"));
    }

    @Test
    void storeProfileImage_shouldRejectEmptyFile() {
        UserImageStorageService service = new UserImageStorageService(tempDir.toString());
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", new byte[0]);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.storeProfileImage(file, 2L));
        assertTrue(ex.getMessage().toLowerCase().contains("vacío"));
    }

    @Test
    void storeProfileImage_shouldRejectNonImage() {
        UserImageStorageService service = new UserImageStorageService(tempDir.toString());
        MockMultipartFile file = new MockMultipartFile("file", "notes.txt", "text/plain", "hello".getBytes());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.storeProfileImage(file, 3L));
        assertTrue(ex.getMessage().toLowerCase().contains("imagen"));
    }

    @Test
    void storeProfileImage_shouldRejectTooLargeFile() {
        UserImageStorageService service = new UserImageStorageService(tempDir.toString());
        byte[] big = new byte[5 * 1024 * 1024 + 1];
        MockMultipartFile file = new MockMultipartFile("file", "avatar.jpg", "image/jpeg", big);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.storeProfileImage(file, 4L));
        assertTrue(ex.getMessage().toLowerCase().contains("tamaño"));
    }

    @Test
    void resolveProfileImagePath_shouldBuildAbsolutePath() {
        UserImageStorageService service = new UserImageStorageService(tempDir.toString());
        String filename = "user_10_123.png";

        Path resolved = service.resolveProfileImagePath(filename);

        assertEquals(tempDir.resolve(filename).normalize(), resolved);
    }
}

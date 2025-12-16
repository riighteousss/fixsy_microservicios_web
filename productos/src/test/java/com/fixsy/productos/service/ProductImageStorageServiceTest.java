package com.fixsy.productos.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductImageStorageServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void storeMainImage_shouldSaveValidImage() throws IOException {
        ProductImageStorageService service = new ProductImageStorageService(tempDir.toString(), "http://test-server:8083");
        MockMultipartFile file = new MockMultipartFile("file", "main.png", "image/png", "img".getBytes());

        String stored = service.storeMainImage(file, 10L);

        Path storedPath = tempDir.resolve(stored);
        assertTrue(Files.exists(storedPath));
        assertTrue(stored.startsWith("product_10_main_"));
        assertTrue(stored.endsWith(".png"));
    }

    @Test
    void storeMainImage_shouldRejectEmptyFile() {
        ProductImageStorageService service = new ProductImageStorageService(tempDir.toString(), "http://test-server:8083");
        MockMultipartFile file = new MockMultipartFile("file", "main.png", "image/png", new byte[0]);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.storeMainImage(file, 11L));
        assertTrue(ex.getMessage().toLowerCase().contains("vacío"));
    }

    @Test
    void storeMainImage_shouldRejectNonImage() {
        ProductImageStorageService service = new ProductImageStorageService(tempDir.toString(), "http://test-server:8083");
        MockMultipartFile file = new MockMultipartFile("file", "main.txt", "text/plain", "text".getBytes());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.storeMainImage(file, 12L));
        assertTrue(ex.getMessage().toLowerCase().contains("imagen"));
    }

    @Test
    void storeMainImage_shouldRejectTooLarge() {
        ProductImageStorageService service = new ProductImageStorageService(tempDir.toString(), "http://test-server:8083");
        byte[] big = new byte[5 * 1024 * 1024 + 1];
        MockMultipartFile file = new MockMultipartFile("file", "main.jpg", "image/jpeg", big);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.storeMainImage(file, 13L));
        assertTrue(ex.getMessage().toLowerCase().contains("tamaño"));
    }

    @Test
    void storeGalleryImages_shouldStoreMultiple() throws IOException {
        ProductImageStorageService service = new ProductImageStorageService(tempDir.toString(), "http://test-server:8083");
        MockMultipartFile img1 = new MockMultipartFile("files", "g1.png", "image/png", "a".getBytes());
        MockMultipartFile img2 = new MockMultipartFile("files", "g2.png", "image/png", "b".getBytes());

        List<String> stored = service.storeGalleryImages(List.of(img1, img2), 20L);

        assertEquals(2, stored.size());
        for (String name : stored) {
            assertTrue(Files.exists(tempDir.resolve(name)));
        }
    }

    @Test
    void resolveProductImagePath_shouldBuildAbsolutePath() {
        ProductImageStorageService service = new ProductImageStorageService(tempDir.toString(), "http://test-server:8083");
        String filename = "product_7_main_1.png";

        Path resolved = service.resolveProductImagePath(filename);

        assertEquals(tempDir.resolve(filename).normalize(), resolved);
    }
}

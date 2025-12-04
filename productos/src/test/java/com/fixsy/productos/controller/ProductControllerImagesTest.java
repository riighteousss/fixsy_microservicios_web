package com.fixsy.productos.controller;

import com.fixsy.productos.model.Product;
import com.fixsy.productos.repository.ProductRepository;
import com.fixsy.productos.service.ProductImageStorageService;
import com.fixsy.productos.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false) // desactiva filtros de seguridad para el test
@SuppressWarnings("removal")
class ProductControllerImagesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private ProductImageStorageService productImageStorageService;

    @Test
    @WithMockUser(roles = "ADMIN") // Simula un usuario con rol ADMIN
    void uploadMainImage_shouldUpdateImagen() throws Exception {
        Product product = buildProduct(10L);
        MockMultipartFile file = new MockMultipartFile("file", "main.png", "image/png", "img".getBytes());
        given(productRepository.findById(10L)).willReturn(Optional.of(product));
        given(productImageStorageService.storeMainImage(any(), eq(10L))).willReturn("product_10_main.png");

        mockMvc.perform(multipart("/api/products/10/image-main").file(file))
                .andExpect(status().isOk());

        verify(productService).setMainImage(10L, "product_10_main.png");
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Simula un usuario con rol ADMIN
    void uploadMainImage_shouldReturn404WhenProductNotFound() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "main.png", "image/png", "img".getBytes());
        given(productRepository.findById(404L)).willReturn(Optional.empty());

        mockMvc.perform(multipart("/api/products/404/image-main").file(file))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Simula un usuario con rol ADMIN
    void uploadMainImage_shouldReturn400WhenInvalidFile() throws Exception {
        Product product = buildProduct(11L);
        MockMultipartFile file = new MockMultipartFile("file", "main.txt", "text/plain", "no".getBytes());
        given(productRepository.findById(11L)).willReturn(Optional.of(product));
        given(productImageStorageService.storeMainImage(any(), eq(11L)))
                .willThrow(new IllegalArgumentException("Solo se permiten archivos de imagen"));

        mockMvc.perform(multipart("/api/products/11/image-main").file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Simula un usuario con rol ADMIN
    void uploadGalleryImages_shouldAppendImages() throws Exception {
        Product product = buildProduct(20L);
        product.setImages(List.of("old1.png"));
        MockMultipartFile f1 = new MockMultipartFile("files", "g1.png", "image/png", "a".getBytes());
        MockMultipartFile f2 = new MockMultipartFile("files", "g2.png", "image/png", "b".getBytes());
        given(productRepository.findById(20L)).willReturn(Optional.of(product));
        given(productImageStorageService.storeGalleryImages(any(), eq(20L)))
                .willReturn(List.of("new1.png", "new2.png"));

        mockMvc.perform(multipart("/api/products/20/images").file(f1).file(f2))
                .andExpect(status().isOk());

        verify(productService).appendGalleryImages(20L, List.of("new1.png", "new2.png"));
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Simula un usuario con rol ADMIN
    void uploadGalleryImages_shouldReturn404WhenProductNotFound() throws Exception {
        MockMultipartFile f1 = new MockMultipartFile("files", "g1.png", "image/png", "a".getBytes());
        given(productRepository.findById(999L)).willReturn(Optional.empty());

        mockMvc.perform(multipart("/api/products/999/images").file(f1))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Simula un usuario con rol ADMIN
    void uploadGalleryImages_shouldReturn400WhenStorageRejects() throws Exception {
        Product product = buildProduct(21L);
        MockMultipartFile f1 = new MockMultipartFile("files", "g1.txt", "text/plain", "a".getBytes());
        given(productRepository.findById(21L)).willReturn(Optional.of(product));
        given(productImageStorageService.storeGalleryImages(any(), eq(21L)))
                .willThrow(new IllegalArgumentException("Solo se permiten archivos de imagen"));

        mockMvc.perform(multipart("/api/products/21/images").file(f1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMainImage_shouldReturnBytes() throws Exception {
        Product product = buildProduct(30L);
        product.setImageUrl("product_30_main.png");
        Path temp = Files.createTempFile("product_30_main", ".png");
        Files.writeString(temp, "imgdata");

        given(productRepository.findById(30L)).willReturn(Optional.of(product));
        given(productImageStorageService.resolveProductImagePath("product_30_main.png")).willReturn(temp);

        mockMvc.perform(get("/api/products/30/image-main"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Type"));
    }

    @Test
    void getMainImage_shouldReturn404WhenNoImage() throws Exception {
        Product product = buildProduct(31L);
        product.setImageUrl(null);
        given(productRepository.findById(31L)).willReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/31/image-main"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMainImage_shouldReturn404WhenProductNotFound() throws Exception {
        given(productRepository.findById(500L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/products/500/image-main"))
                .andExpect(status().isNotFound());
    }

    private Product buildProduct(Long id) {
        Product p = new Product();
        p.setId(id);
        p.setNombre("Producto");
        p.setPrecioNormal(java.math.BigDecimal.TEN);
        p.setStock(10);
        p.setIsActive(true);
        p.setIsFeatured(false);
        p.setCreatedAt(LocalDateTime.now());
        return p;
    }
}

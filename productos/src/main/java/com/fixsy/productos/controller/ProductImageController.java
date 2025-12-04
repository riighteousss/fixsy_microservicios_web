package com.fixsy.productos.controller;

import com.fixsy.productos.dto.ProductImageInfoDTO;
import com.fixsy.productos.model.Product;
import com.fixsy.productos.repository.ProductRepository;
import com.fixsy.productos.service.ProductImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@CrossOrigin(origins = "*")
@Tag(name = "Product Images", description = "Carga, listado y entrega de imagenes de productos")
public class ProductImageController {

    private final ProductRepository productRepository;
    private final ProductImageStorageService productImageStorageService;
    private static final String PUBLIC_IMAGE_PREFIX = "/images/";

    public ProductImageController(ProductRepository productRepository,
                                  ProductImageStorageService productImageStorageService) {
        this.productRepository = productRepository;
        this.productImageStorageService = productImageStorageService;
    }

    @Operation(summary = "Listar imagenes publicas de productos activos")
    @GetMapping("/api/products/images")
    public ResponseEntity<List<ProductImageInfoDTO>> listProductImages() {
        List<Product> products = productRepository.findByIsActiveTrue();
        List<ProductImageInfoDTO> payload = products.stream()
                .map(this::toImageInfo)
                .collect(Collectors.toList());
        return ResponseEntity.ok(payload);
    }

    private ProductImageInfoDTO toImageInfo(Product product) {
        String mainImage = productImageStorageService.buildPublicImagePath(product.getImageUrl());
        List<String> galleryImages = productImageStorageService.buildPublicImagePaths(product.getImages());
        List<String> merged = new ArrayList<>(galleryImages);
        if (mainImage != null && !merged.contains(mainImage)) {
            merged.add(0, mainImage);
        }
        return new ProductImageInfoDTO(product.getId(), mainImage, merged);
    }

    @PostMapping(value = "/api/upload/products/{productId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir imagen principal de producto", description = "Retorna la ruta publica (/images/...)")
    public ResponseEntity<String> uploadProductImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("Archivo vacio");
        }

        try {
            String storedName = productImageStorageService.storeMainImage(file, productId);
            return ResponseEntity.ok(PUBLIC_IMAGE_PREFIX + storedName);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir imagen: " + e.getMessage());
        }
    }

    @GetMapping("/images/{fileName:.+}")
    public ResponseEntity<Resource> serveProductImage(@PathVariable String fileName) {
        try {
            // Log para debugging
            System.out.println("Serving image: " + fileName);
            
            Path imagePath = productImageStorageService.resolveProductImagePath(fileName).normalize();
            System.out.println("Image path: " + imagePath);
            System.out.println("File exists: " + Files.exists(imagePath));
            
            if (!Files.exists(imagePath)) {
                System.out.println("Image not found at: " + imagePath);
                throw new ResponseStatusException(NOT_FOUND, "Imagen no encontrada: " + fileName);
            }

            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                // Intentar detectar por extensión
                String lowerName = fileName.toLowerCase();
                if (lowerName.endsWith(".png")) contentType = "image/png";
                else if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) contentType = "image/jpeg";
                else if (lowerName.endsWith(".gif")) contentType = "image/gif";
                else if (lowerName.endsWith(".webp")) contentType = "image/webp";
                else contentType = "image/jpeg"; // default
            }
            
            MediaType mediaType = MediaType.parseMediaType(contentType);

            InputStreamResource resource = new InputStreamResource(Files.newInputStream(imagePath));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                    .body(resource);
        } catch (IOException e) {
            System.out.println("Error reading image: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, "No se pudo leer la imagen: " + fileName, e);
        }
    }
}

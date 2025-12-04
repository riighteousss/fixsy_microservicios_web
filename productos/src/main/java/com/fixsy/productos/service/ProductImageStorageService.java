package com.fixsy.productos.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductImageStorageService {

    private final Path baseDirectory;
    private static final String PUBLIC_IMAGE_PREFIX = "/images/";

    public ProductImageStorageService(@Value("${file.upload-dir.products:src/main/java/com/fixsy/productos/images}") String uploadDir) {
        this.baseDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.baseDirectory);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear el directorio de subida de productos", e);
        }
    }

    public String storeMainImage(MultipartFile file, Long productId) throws IOException {
        validateImage(file);

        String extension = getExtensionFromContentType(file.getContentType());
        String fileName = "product_" + productId + "_main_" + System.currentTimeMillis() + extension;

        Path target = this.baseDirectory.resolve(fileName).normalize();
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    public List<String> storeGalleryImages(List<MultipartFile> files, Long productId) throws IOException {
        List<String> stored = new ArrayList<>();
        int index = 1;
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            validateImage(file);
            String extension = getExtensionFromContentType(file.getContentType());
            String fileName = "product_" + productId + "_g" + index++ + "_" + System.currentTimeMillis() + extension;

            Path target = this.baseDirectory.resolve(fileName).normalize();
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            stored.add(fileName);
        }
        return stored;
    }

    public Path resolveProductImagePath(String storedPath) {
        return this.baseDirectory.resolve(storedPath).normalize();
    }

    private void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Solo se permiten archivos de imagen");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("La imagen excede el tamaño máximo permitido (5MB)");
        }
    }

    private String getExtensionFromContentType(String contentType) {
        if ("image/png".equals(contentType)) return ".png";
        if ("image/jpeg".equals(contentType) || "image/jpg".equals(contentType)) return ".jpg";
        if ("image/gif".equals(contentType)) return ".gif";
        return ".img";
    }

    public String buildPublicImagePath(String storedName) {
        if (storedName == null || storedName.isBlank()) {
            return null;
        }
        if (storedName.startsWith(PUBLIC_IMAGE_PREFIX)) {
            return storedName;
        }
        return PUBLIC_IMAGE_PREFIX + storedName;
    }

    public List<String> buildPublicImagePaths(List<String> storedPaths) {
        if (storedPaths == null || storedPaths.isEmpty()) {
            return Collections.emptyList();
        }
        return storedPaths.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(this::buildPublicImagePath)
                .distinct()
                .collect(Collectors.toList());
    }
}

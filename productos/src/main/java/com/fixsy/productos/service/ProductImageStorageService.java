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
    private final String publicBaseUrl;
    private static final String PUBLIC_IMAGE_PREFIX = "/images/";

    public ProductImageStorageService(
            @Value("${file.upload-dir.products:src/main/java/com/fixsy/productos/images}") String uploadDir,
            @Value("${file.public-base-url:http://localhost:${server.port}}") String publicBaseUrl) {
        this.baseDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.baseDirectory);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear el directorio de subida de productos", e);
        }
        this.publicBaseUrl = normalizePublicBaseUrl(publicBaseUrl);
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
        if (storedPath == null || storedPath.isBlank()) {
            throw new IllegalArgumentException("El nombre de imagen es requerido");
        }
        if (isAbsoluteWebUrl(storedPath)) {
            throw new IllegalArgumentException("La imagen proviene de una URL externa y no se puede resolver localmente");
        }
        String sanitized = stripImagePublicPrefix(storedPath);
        if (sanitized.isBlank()) {
            throw new IllegalArgumentException("El nombre de la imagen es invalido");
        }
        return this.baseDirectory.resolve(sanitized).normalize();
    }

    private void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo estケ vacヴo");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Solo se permiten archivos de imagen");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("La imagen excede el tamaヵo mケximo permitido (5MB)");
        }
    }

    private String getExtensionFromContentType(String contentType) {
        if ("image/png".equals(contentType)) return ".png";
        if ("image/jpeg".equals(contentType) || "image/jpg".equals(contentType)) return ".jpg";
        if ("image/gif".equals(contentType)) return ".gif";
        return ".img";
    }

    public String buildPublicImagePath(String storedName) {
        if (storedName == null) {
            return null;
        }
        String trimmed = storedName.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (isAbsoluteWebUrl(trimmed)) {
            return trimmed;
        }
        String sanitizedName = stripImagePublicPrefix(trimmed);
        if (sanitizedName.isEmpty()) {
            return null;
        }
        return publicBaseUrl + PUBLIC_IMAGE_PREFIX + sanitizedName;
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
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private String stripImagePublicPrefix(String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.startsWith(PUBLIC_IMAGE_PREFIX)) {
            trimmed = trimmed.substring(PUBLIC_IMAGE_PREFIX.length());
        }
        return trimmed;
    }

    private boolean isAbsoluteWebUrl(String value) {
        if (value == null) {
            return false;
        }
        String lower = value.trim().toLowerCase();
        return lower.startsWith("http://") || lower.startsWith("https://");
    }

    private String normalizePublicBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "http://localhost:8083";
        }
        String normalized = baseUrl.trim();
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}

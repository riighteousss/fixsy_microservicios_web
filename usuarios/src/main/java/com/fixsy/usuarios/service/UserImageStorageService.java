package com.fixsy.usuarios.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class UserImageStorageService {

    private final Path baseDirectory;

    public UserImageStorageService(@Value("${file.upload-dir.profile:uploads/profile}") String uploadDir) {
        this.baseDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.baseDirectory);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear el directorio de subida de perfiles", e);
        }
    }

    public String storeProfileImage(MultipartFile file, Long userId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Solo se permiten archivos de imagen");
        }

        // tamaño máximo aproximado: 5MB
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("La imagen excede el tamaño máximo permitido (5MB)");
        }

        String extension = getExtensionFromContentType(contentType);
        String fileName = "user_" + userId + "_" + System.currentTimeMillis() + extension;

        Path target = this.baseDirectory.resolve(fileName).normalize();
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // Guardamos solo la ruta relativa respecto a la carpeta base
        return fileName;
    }

    public Path resolveProfileImagePath(String storedPath) {
        return this.baseDirectory.resolve(storedPath).normalize();
    }

    private String getExtensionFromContentType(String contentType) {
        if ("image/png".equals(contentType)) return ".png";
        if ("image/jpeg".equals(contentType) || "image/jpg".equals(contentType)) return ".jpg";
        if ("image/gif".equals(contentType)) return ".gif";
        return ".img";
    }
}

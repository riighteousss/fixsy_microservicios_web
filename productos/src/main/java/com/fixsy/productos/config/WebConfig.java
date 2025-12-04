package com.fixsy.productos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir.products:src/main/java/com/fixsy/productos/images}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(uploadDir).toAbsolutePath().normalize().toString();
        // Asegurar que termine con separador
        String separator = System.getProperty("file.separator");
        if (!absolutePath.endsWith(separator)) {
            absolutePath = absolutePath + separator;
        }
        
        // Log para debugging (puedes removerlo después)
        System.out.println("Serving images from: file:" + absolutePath);
        
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + absolutePath)
                .setCachePeriod(3600); // cache 1h
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}

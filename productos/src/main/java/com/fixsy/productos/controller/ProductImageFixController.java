package com.fixsy.productos.controller;

import com.fixsy.productos.model.Product;
import com.fixsy.productos.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
@Tag(name = "Product Image Fix", description = "Utilidad para arreglar rutas de imágenes")
public class ProductImageFixController {

    @Autowired
    private ProductRepository productRepository;

    /**
     * Mapeo de productos a imágenes existentes basado en el nombre del producto
     */
    private static final Map<String, String> PRODUCT_IMAGE_MAP = new HashMap<>();
    
    static {
        PRODUCT_IMAGE_MAP.put("Filtro de aceite", "/images/filtros2.webp");
        PRODUCT_IMAGE_MAP.put("Pastillas de freno", "/images/png-clipart-car-brake-pad-disc-brake-vehicle-car-car-automobile-repair-shop.png");
        PRODUCT_IMAGE_MAP.put("Amortiguador", "/images/suspension.png");
        PRODUCT_IMAGE_MAP.put("Bateria", "/images/electricidad.jpg");
        PRODUCT_IMAGE_MAP.put("Aceite sintetico", "/images/png-transparent-car-oil-motor-oil-lubricant-engine-twostroke-engine-lubrication-base.png");
        PRODUCT_IMAGE_MAP.put("Filtro de aire", "/images/pngtree-truck-fuel-oil-filter-png-image_11484952.png");
        PRODUCT_IMAGE_MAP.put("Bujias", "/images/electricidad.jpg");
        PRODUCT_IMAGE_MAP.put("Disco de freno", "/images/png-clipart-car-brake-pad-disc-brake-vehicle-car-car-automobile-repair-shop.png");
        PRODUCT_IMAGE_MAP.put("Kit correa", "/images/kit tensor motor.jpg");
        PRODUCT_IMAGE_MAP.put("Liquido de frenos", "/images/las-mejores-7-marcas-de-los-mejores-liquidos-de-frenos.jpg");
        PRODUCT_IMAGE_MAP.put("Filtro de combustible", "/images/tres-filtros-aceite-motor-automovil_207928-40.avif");
    }

    @PostMapping("/admin/fix-images")
    @Operation(summary = "Arreglar rutas de imágenes de productos para usar archivos existentes")
    public ResponseEntity<Map<String, Object>> fixProductImages() {
        List<Product> products = productRepository.findAll();
        int fixed = 0;
        int skipped = 0;
        
        for (Product product : products) {
            String currentImage = product.getImageUrl();
            
            // Si ya tiene una ruta válida que existe, saltar
            if (currentImage != null && currentImage.startsWith("/images/")) {
                // Verificar si el archivo existe realmente
                String fileName = currentImage.replace("/images/", "");
                if (fileName.contains("product_") && fileName.contains("_main_")) {
                    // Es una ruta generada que probablemente no existe, buscar reemplazo
                    String newImage = findImageForProduct(product.getNombre());
                    if (newImage != null && !newImage.equals(currentImage)) {
                        product.setImageUrl(newImage);
                        productRepository.save(product);
                        fixed++;
                    } else {
                        skipped++;
                    }
                } else {
                    skipped++;
                }
            } else {
                // No tiene imagen o tiene ruta inválida, buscar una
                String newImage = findImageForProduct(product.getNombre());
                if (newImage != null) {
                    product.setImageUrl(newImage);
                    productRepository.save(product);
                    fixed++;
                } else {
                    skipped++;
                }
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("fixed", fixed);
        result.put("skipped", skipped);
        result.put("total", products.size());
        result.put("message", "Rutas de imágenes actualizadas");
        
        return ResponseEntity.ok(result);
    }

    private String findImageForProduct(String productName) {
        if (productName == null) return null;
        
        String name = productName.toLowerCase();
        
        // Buscar coincidencias en el mapa
        for (Map.Entry<String, String> entry : PRODUCT_IMAGE_MAP.entrySet()) {
            if (name.contains(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }
        
        // Mapeos específicos por palabras clave
        if (name.contains("filtro") && name.contains("aceite")) {
            return "/images/filtros2.webp";
        } else if (name.contains("filtro") && name.contains("aire")) {
            return "/images/pngtree-truck-fuel-oil-filter-png-image_11484952.png";
        } else if (name.contains("filtro") && name.contains("combustible")) {
            return "/images/tres-filtros-aceite-motor-automovil_207928-40.avif";
        } else if (name.contains("pastilla") || name.contains("freno")) {
            return "/images/png-clipart-car-brake-pad-disc-brake-vehicle-car-car-automobile-repair-shop.png";
        } else if (name.contains("disco") && name.contains("freno")) {
            return "/images/png-clipart-car-brake-pad-disc-brake-vehicle-car-car-automobile-repair-shop.png";
        } else if (name.contains("liquido") && name.contains("freno")) {
            return "/images/las-mejores-7-marcas-de-los-mejores-liquidos-de-frenos.jpg";
        } else if (name.contains("amortiguador")) {
            return "/images/suspension.png";
        } else if (name.contains("bateria")) {
            return "/images/electricidad.jpg";
        } else if (name.contains("aceite")) {
            return "/images/png-transparent-car-oil-motor-oil-lubricant-engine-twostroke-engine-lubrication-base.png";
        } else if (name.contains("bujia")) {
            return "/images/electricidad.jpg";
        } else if (name.contains("correa") || name.contains("tensor")) {
            return "/images/kit tensor motor.jpg";
        }
        
        return null;
    }
}


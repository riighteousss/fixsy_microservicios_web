package com.fixsy.productos.controller;

import com.fixsy.productos.dto.ProductDTO;
import com.fixsy.productos.dto.ImageUrlUpdateDTO;
import com.fixsy.productos.dto.ProductRequestDTO;
import com.fixsy.productos.model.Product;
import com.fixsy.productos.repository.ProductRepository;
import com.fixsy.productos.service.ProductImageStorageService;
import com.fixsy.productos.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // Para desarrollo web - En produccion especificar dominios
@Tag(name = "Product Controller", description = "API para gestion de productos/repuestos de Fixsy Parts")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageStorageService productImageStorageService;

    @GetMapping("/test")
    @Operation(summary = "Endpoint de diagnostico que devuelve lista vacia")
    public ResponseEntity<List<ProductDTO>> testProducts() {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping
    @Operation(summary = "Obtener todos los productos activos")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/all")
    @Operation(summary = "Obtener todos los productos (incluyendo inactivos) - Solo Admin")
    public ResponseEntity<List<ProductDTO>> getAllProductsIncludeInactive() {
        return ResponseEntity.ok(productService.getAllProductsIncludeInactive());
    }

    @PostMapping(value = "/admin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RolesAllowed("ADMIN")
    @Operation(summary = "Crear producto (admin, multipart/form-data)")
    public ResponseEntity<ProductDTO> createProductAdmin(
            @RequestPart("product") @Valid ProductRequestDTO productRequest,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        return new ResponseEntity<>(productService.createProduct(productRequest, imageFile), HttpStatus.CREATED);
    }

    @PutMapping(value = "/admin/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RolesAllowed("ADMIN")
    @Operation(summary = "Actualizar producto (admin, multipart/form-data)")
    public ResponseEntity<ProductDTO> updateProductAdmin(
            @PathVariable Long id,
            @RequestPart("product") @Valid ProductRequestDTO productRequest,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        return ResponseEntity.ok(productService.updateProduct(id, productRequest, imageFile));
    }

    @GetMapping("/admin")
    @RolesAllowed("ADMIN")
    @Operation(summary = "Listar todos los productos para panel admin")
    public ResponseEntity<List<ProductDTO>> getAllProductsAdmin() {
        return ResponseEntity.ok(productService.getAllProductsIncludeInactive());
    }

    @PutMapping("/{id}/image-url")
    @RolesAllowed("ADMIN")
    @Operation(summary = "Actualizar la URL de la imagen principal (solo admin)")
    public ResponseEntity<ProductDTO> updateImageUrl(
            @PathVariable Long id,
            @RequestBody ImageUrlUpdateDTO request) {
        return ResponseEntity.ok(productService.updateImageUrl(id, request));
    }

    @PostMapping("/admin/normalize-data")
    @RolesAllowed("ADMIN")
    @Operation(summary = "Normalizar datos de productos (solo admin)")
    public ResponseEntity<String> normalizeProductData() {
        productService.normalizeProductSlugs();
        return ResponseEntity.ok("Datos de productos normalizados correctamente");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Obtener producto por SKU")
    public ResponseEntity<ProductDTO> getProductBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productService.getProductBySku(sku));
    }

    @GetMapping("/featured")
    @Operation(summary = "Obtener productos destacados")
    public ResponseEntity<List<ProductDTO>> getFeaturedProducts() {
        return ResponseEntity.ok(productService.getFeaturedProducts());
    }

    @GetMapping("/on-sale")
    @Operation(summary = "Obtener productos en oferta")
    public ResponseEntity<List<ProductDTO>> getProductsOnSale() {
        return ResponseEntity.ok(productService.getProductsOnSale());
    }

    @GetMapping("/category/{categoria}")
    @Operation(summary = "Obtener productos por categoria")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable String categoria) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoria));
    }

    @GetMapping("/marca/{marca}")
    @Operation(summary = "Obtener productos por marca")
    public ResponseEntity<List<ProductDTO>> getProductsByMarca(@PathVariable String marca) {
        return ResponseEntity.ok(productService.getProductsByMarca(marca));
    }

    @GetMapping("/tag/{tag}")
    @Operation(summary = "Obtener productos por tag")
    public ResponseEntity<List<ProductDTO>> getProductsByTag(@PathVariable String tag) {
        return ResponseEntity.ok(productService.getProductsByTag(tag));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar productos por nombre")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String q) {
        return ResponseEntity.ok(productService.searchProducts(q));
    }

    @GetMapping("/in-stock")
    @Operation(summary = "Obtener productos con stock disponible")
    public ResponseEntity<List<ProductDTO>> getProductsInStock() {
        return ResponseEntity.ok(productService.getProductsInStock());
    }

    @GetMapping("/out-of-stock")
    @Operation(summary = "Obtener productos sin stock")
    public ResponseEntity<List<ProductDTO>> getProductsOutOfStock() {
        return ResponseEntity.ok(productService.getProductsOutOfStock());
    }

    @GetMapping("/categorias")
    @Operation(summary = "Obtener lista de categorias disponibles")
    public ResponseEntity<List<String>> getAllCategorias() {
        return ResponseEntity.ok(productService.getAllCategorias());
    }

    @GetMapping("/marcas")
    @Operation(summary = "Obtener lista de marcas disponibles")
    public ResponseEntity<List<String>> getAllMarcas() {
        return ResponseEntity.ok(productService.getAllMarcas());
    }

    @PostMapping
    @Operation(summary = "Crear nuevo producto")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductRequestDTO productRequest) {
        return new ResponseEntity<>(productService.createProduct(productRequest, null), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id, 
            @Valid @RequestBody ProductRequestDTO productRequest) {
        return ResponseEntity.ok(productService.updateProduct(id, productRequest, null));
    }

    @PatchMapping("/{id}/price")
    @Operation(summary = "Actualizar precio del producto")
    public ResponseEntity<ProductDTO> updatePrice(
            @PathVariable Long id,
            @RequestBody UpdatePriceRequest request) {
        return ResponseEntity.ok(productService.updatePrice(id, request.precioNormal()));
    }

    @PatchMapping("/{id}/offer")
    @Operation(summary = "Actualizar oferta del producto")
    public ResponseEntity<ProductDTO> updateOffer(
            @PathVariable Long id,
            @RequestBody UpdateOfferRequest request) {
        return ResponseEntity.ok(productService.updateOffer(id, request.precioOferta()));
    }

    @PutMapping("/{id}/stock")
    @Operation(summary = "Actualizar stock del producto")
    public ResponseEntity<ProductDTO> updateStock(
            @PathVariable Long id,
            @RequestParam Integer stock) {
        return ResponseEntity.ok(productService.updateStock(id, stock));
    }

    @PutMapping("/{id}/stock/adjust")
    @Operation(summary = "Ajustar stock del producto (sumar/restar)")
    public ResponseEntity<ProductDTO> adjustStock(
            @PathVariable Long id,
            @RequestParam Integer adjustment) {
        return ResponseEntity.ok(productService.adjustStock(id, adjustment));
    }

    @PutMapping("/{id}/featured")
    @Operation(summary = "Alternar estado de producto destacado")
    public ResponseEntity<ProductDTO> toggleFeatured(@PathVariable Long id) {
        return ResponseEntity.ok(productService.toggleFeatured(id));
    }

    @PutMapping("/{id}/active")
    @Operation(summary = "Alternar estado activo del producto")
    public ResponseEntity<ProductDTO> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(productService.toggleActive(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/image-main")
    @Operation(summary = "Subir imagen principal del producto (multipart/form-data)")
    public ResponseEntity<ProductDTO> uploadMainImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

            String storedPath = productImageStorageService.storeMainImage(file, id);
            ProductDTO dto = productService.setMainImage(product.getId(), storedPath);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la imagen", e);
        }
    }

    @PostMapping("/{id}/images")
    @Operation(summary = "Subir imagenes de galeria del producto (multipart/form-data)")
    public ResponseEntity<ProductDTO> uploadGalleryImages(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

            List<String> storedPaths = productImageStorageService.storeGalleryImages(files, id);
            if (storedPaths.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se recibieron imagenes validas");
            }
            ProductDTO dto = productService.appendGalleryImages(product.getId(), storedPaths);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar las imagenes", e);
        }
    }

    @DeleteMapping("/{id}/image")
    @Operation(summary = "Eliminar imagen principal del producto")
    public ResponseEntity<ProductDTO> deleteProductImage(@PathVariable Long id) {
        return ResponseEntity.ok(productService.deleteProductImage(id));
    }

    @GetMapping("/{id}/image-main")
    @Operation(summary = "Obtener imagen principal del producto")
    public ResponseEntity<byte[]> getMainImage(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        String storedPath = product.getImageUrl();
        if (storedPath == null || storedPath.isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto no tiene imagen principal");
        }

        Path imagePath = productImageStorageService.resolveProductImagePath(storedPath);
        if (!Files.exists(imagePath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Imagen no encontrada en el servidor");
        }

        try {
            byte[] bytes = Files.readAllBytes(imagePath);
            String contentType = Files.probeContentType(imagePath);
            if (contentType == null) {
                contentType = "image/jpeg";
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(bytes);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al leer la imagen", e);
        }
    }

    public record UpdatePriceRequest(java.math.BigDecimal precioNormal) { }
    public record UpdateOfferRequest(java.math.BigDecimal precioOferta) { }
}

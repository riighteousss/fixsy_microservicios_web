package com.fixsy.productos.controller;

import com.fixsy.productos.dto.ProductDTO;
import com.fixsy.productos.dto.ProductRequestDTO;
import com.fixsy.productos.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*") // Para desarrollo web - En producción especificar dominios
@Tag(name = "Product Controller", description = "API para gestión de productos/repuestos de Fixsy Parts")
public class ProductController {
    @Autowired
    private ProductService productService;

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
    @Operation(summary = "Obtener productos por categoría")
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
    @Operation(summary = "Obtener lista de categorías disponibles")
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
        return new ResponseEntity<>(productService.createProduct(productRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id, 
            @Valid @RequestBody ProductRequestDTO productRequest) {
        return ResponseEntity.ok(productService.updateProduct(id, productRequest));
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
}


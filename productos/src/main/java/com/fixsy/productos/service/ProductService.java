package com.fixsy.productos.service;

import com.fixsy.productos.dto.ProductDTO;
import com.fixsy.productos.dto.ProductRequestDTO;
import com.fixsy.productos.model.Product;
import com.fixsy.productos.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<ProductDTO> getAllProducts() {
        return productRepository.findByIsActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getAllProductsIncludeInactive() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return convertToDTO(product);
    }

    public ProductDTO getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return convertToDTO(product);
    }

    public List<ProductDTO> getFeaturedProducts() {
        return productRepository.findByIsFeaturedTrueAndIsActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsOnSale() {
        return productRepository.findProductsOnSale().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsByCategory(String categoria) {
        return productRepository.findByCategoriaAndIsActiveTrue(categoria).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsByMarca(String marca) {
        return productRepository.findByMarcaAndIsActiveTrue(marca).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsByTag(String tag) {
        return productRepository.findByTag(tag).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> searchProducts(String searchTerm) {
        return productRepository.searchByNombre(searchTerm).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsInStock() {
        return productRepository.findByStockGreaterThanAndIsActiveTrue(0).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsOutOfStock() {
        return productRepository.findByStockLessThanEqualAndIsActiveTrue(0).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<String> getAllCategorias() {
        return productRepository.findAllCategorias();
    }

    public List<String> getAllMarcas() {
        return productRepository.findAllMarcas();
    }

    @Transactional
    public ProductDTO createProduct(ProductRequestDTO productRequest) {
        if (productRequest.getSku() != null && productRepository.existsBySku(productRequest.getSku())) {
            throw new RuntimeException("Ya existe un producto con este SKU");
        }

        Product product = new Product();
        mapRequestToEntity(productRequest, product);

        Product savedProduct = productRepository.save(product);
        return convertToDTO(savedProduct);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductRequestDTO productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Verificar SKU único si cambió
        if (productRequest.getSku() != null && 
            !productRequest.getSku().equals(product.getSku()) && 
            productRepository.existsBySku(productRequest.getSku())) {
            throw new RuntimeException("Ya existe un producto con este SKU");
        }

        mapRequestToEntity(productRequest, product);

        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    @Transactional
    public ProductDTO updateStock(Long id, Integer newStock) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        if (newStock < 0) {
            throw new RuntimeException("El stock no puede ser negativo");
        }
        
        product.setStock(newStock);
        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    @Transactional
    public ProductDTO adjustStock(Long id, Integer adjustment) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        int newStock = product.getStock() + adjustment;
        if (newStock < 0) {
            throw new RuntimeException("Stock insuficiente");
        }
        
        product.setStock(newStock);
        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    @Transactional
    public ProductDTO toggleFeatured(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        product.setIsFeatured(!product.getIsFeatured());
        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    @Transactional
    public ProductDTO toggleActive(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        product.setIsActive(!product.getIsActive());
        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado");
        }
        productRepository.deleteById(id);
    }

    private void mapRequestToEntity(ProductRequestDTO request, Product entity) {
        entity.setNombre(request.getNombre());
        entity.setDescripcion(request.getDescripcion());
        entity.setPrecio(request.getPrecio());
        entity.setPrecioOferta(request.getPrecioOferta());
        entity.setStock(request.getStock());
        entity.setImagen(request.getImagen());
        entity.setCategoria(request.getCategoria());
        entity.setMarca(request.getMarca());
        entity.setSku(request.getSku());
        
        if (request.getIsFeatured() != null) {
            entity.setIsFeatured(request.getIsFeatured());
        }
        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
        }
        
        // Convertir listas a strings separados por comas
        if (request.getTags() != null) {
            entity.setTags(String.join(",", request.getTags()));
        }
        if (request.getImages() != null) {
            entity.setImages(String.join(",", request.getImages()));
        }
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setNombre(product.getNombre());
        dto.setDescripcion(product.getDescripcion());
        dto.setPrecio(product.getPrecio());
        dto.setPrecioOferta(product.getPrecioOferta());
        dto.setStock(product.getStock());
        dto.setImagen(product.getImagen());
        dto.setCategoria(product.getCategoria());
        dto.setMarca(product.getMarca());
        dto.setSku(product.getSku());
        dto.setIsFeatured(product.getIsFeatured());
        dto.setIsActive(product.getIsActive());
        dto.setCreatedAt(product.getCreatedAt());
        
        // Convertir strings separados por comas a listas
        if (product.getTags() != null && !product.getTags().isBlank()) {
            dto.setTags(Arrays.asList(product.getTags().split(",")));
        } else {
            dto.setTags(Collections.emptyList());
        }
        
        if (product.getImages() != null && !product.getImages().isBlank()) {
            dto.setImages(Arrays.asList(product.getImages().split(",")));
        } else {
            dto.setImages(Collections.emptyList());
        }
        
        return dto;
    }
}


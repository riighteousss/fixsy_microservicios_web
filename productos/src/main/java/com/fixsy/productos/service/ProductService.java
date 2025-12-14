package com.fixsy.productos.service;

import com.fixsy.productos.dto.ProductDTO;
import com.fixsy.productos.dto.ImageUrlUpdateDTO;
import com.fixsy.productos.dto.ProductRequestDTO;
import com.fixsy.productos.model.Product;
import com.fixsy.productos.repository.ProductRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductImageStorageService productImageStorageService;

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
    public ProductDTO createProduct(ProductRequestDTO productRequest, MultipartFile imageFile) {
        validateSku(productRequest.getSku());

        Product product = new Product();
        mapRequestToEntity(productRequest, product);
        Product savedProduct = productRepository.save(product);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String storedPath = productImageStorageService.storeMainImage(imageFile, savedProduct.getId());
                savedProduct.setImageUrl(storedPath);
                savedProduct = productRepository.save(savedProduct);
            } catch (Exception e) {
                throw new RuntimeException("No se pudo almacenar la imagen del producto", e);
            }
        }

        return convertToDTO(savedProduct);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductRequestDTO productRequest, MultipartFile imageFile) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        validateSkuOnUpdate(productRequest.getSku(), product.getSku());
        mapRequestToEntity(productRequest, product);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String storedPath = productImageStorageService.storeMainImage(imageFile, product.getId());
                product.setImageUrl(storedPath);
            } catch (Exception e) {
                throw new RuntimeException("No se pudo almacenar la imagen del producto", e);
            }
        }

        Product updatedProduct = productRepository.save(product);
        return convertToDTO(updatedProduct);
    }

    // Compatibilidad con tests existentes (firma sin MultipartFile)
    @Transactional
    public ProductDTO updateProduct(Long id, ProductRequestDTO productRequest) {
        return updateProduct(id, productRequest, null);
    }

    @Transactional
    public ProductDTO updatePrice(Long id, BigDecimal precio) {
        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        product.setPrecioNormal(precio);
        // invalidar oferta si queda mayor al precio
        if (product.getPrecioOferta() != null && product.getPrecioOferta().compareTo(precio) > 0) {
            product.setPrecioOferta(null);
        }
        Product saved = productRepository.save(product);
        return convertToDTO(saved);
    }

    @Transactional
    public ProductDTO updateOffer(Long id, BigDecimal precioOferta) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (precioOferta == null || precioOferta.compareTo(BigDecimal.ZERO) <= 0) {
            product.setPrecioOferta(null);
        } else {
            if (product.getPrecioNormal() != null && precioOferta.compareTo(product.getPrecioNormal()) > 0) {
                throw new IllegalArgumentException("La oferta no puede ser mayor al precio");
            }
            product.setPrecioOferta(precioOferta);
        }
        Product saved = productRepository.save(product);
        return convertToDTO(saved);
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

    public ProductDTO setMainImage(Long id, String storedPath) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        product.setImageUrl(storedPath);
        Product saved = productRepository.save(product);
        return convertToDTO(saved);
    }

    @Transactional
    public ProductDTO updateImageUrl(Long productId, ImageUrlUpdateDTO dto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        product.setImageUrl(dto.getNewImageUrl());
        Product saved = productRepository.save(product);
        return convertToDTO(saved);
    }

    @Transactional
    public void normalizeProductSlugs() {
        List<Product> products = productRepository.findAll();
        Set<String> usedSlugs = products.stream()
                .map(Product::getSlug)
                .filter(slug -> !isSlugInvalid(slug))
                .collect(Collectors.toCollection(HashSet::new));

        List<Product> toUpdate = new ArrayList<>();
        for (Product product : products) {
            if (isSlugInvalid(product.getSlug())) {
                String baseSlug = resolveSlug(null, product.getNombre(), null);
                if (baseSlug == null || baseSlug.isBlank()) {
                    continue;
                }

                String candidate = baseSlug;
                int suffix = 1;
                while (usedSlugs.contains(candidate)) {
                    candidate = baseSlug + "-" + suffix;
                    suffix++;
                }

                product.setSlug(candidate);
                usedSlugs.add(candidate);
                toUpdate.add(product);
            }
        }

        if (!toUpdate.isEmpty()) {
            productRepository.saveAll(toUpdate);
        }
    }

    public ProductDTO appendGalleryImages(Long id, List<String> storedPaths) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        List<String> all = new ArrayList<>(safeList(product.getImages()));
        if (storedPaths != null) {
            for (String path : storedPaths) {
                if (path != null && !path.isBlank()) {
                    all.add(path.trim());
                }
            }
        }

        product.setImages(all);
        Product saved = productRepository.save(product);
        return convertToDTO(saved);
    }

    @Transactional
    public ProductDTO deleteProductImage(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        String storedPath = product.getImageUrl();
        if (storedPath == null || storedPath.isBlank()) {
            throw new RuntimeException("El producto no tiene imagen principal");
        }

        try {
            Path path = productImageStorageService.resolveProductImagePath(storedPath);
            Files.deleteIfExists(path);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo eliminar la imagen del servidor", e);
        }

        product.setImageUrl(null);
        Product saved = productRepository.save(product);
        return convertToDTO(saved);
    }

    private void mapRequestToEntity(ProductRequestDTO request, Product entity) {
        if (request.getPrice() != null && request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        if (request.getStock() != null && request.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        if (request.getDiscountPercentage() != null &&
                (request.getDiscountPercentage() < 0 || request.getDiscountPercentage() > 100)) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 100");
        }

        entity.setNombre(request.getName());
        entity.setSlug(resolveSlug(null, request.getName(), entity.getSlug()));
        entity.setDescripcionCorta(request.getDescription());
        entity.setDescripcionLarga(request.getDescription());
        entity.setPrecioNormal(request.getPrice());
        entity.setPrecioOferta(null);
        entity.setStock(request.getStock() == null ? 0 : request.getStock());
        entity.setCategoria(defaultCategoria(entity.getCategoria(), "Accesorios"));
        entity.setCategoryId(request.getCategoryId());
        entity.setTagIds(request.getTagIds() == null ? new ArrayList<>() : new ArrayList<>(request.getTagIds()));
        entity.setTags(new ArrayList<>());
        entity.setMarca(entity.getMarca());
        if (request.getSku() != null && !request.getSku().isBlank()) {
            entity.setSku(request.getSku());
        }

        if (request.getIsFeatured() != null) {
            entity.setIsFeatured(request.getIsFeatured());
        }
        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
        }

        if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            entity.setImageUrl(request.getImageUrl());
        }

        entity.setDiscountPercentage(request.getDiscountPercentage() == null ? 0 : request.getDiscountPercentage());
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setNombre(product.getNombre());
        dto.setSlug(resolveSlug(product.getSlug(), product.getNombre(), null));
        dto.setDescripcionCorta(fallbackDescripcionCorta(product));
        dto.setDescripcionLarga(product.getDescripcionLarga());
        dto.setPrecioNormal(product.getPrecioNormal());
        dto.setPrecioOferta(product.getPrecioOferta());
        dto.setDiscountPercentage(product.getDiscountPercentage());
        dto.setFinalPrice(calculateFinalPrice(product));
        dto.setStock(product.getStock() == null ? 0 : product.getStock());
        dto.setImageUrl(productImageStorageService.buildPublicImagePath(product.getImageUrl()));
        dto.setCategoria(defaultCategoria(product.getCategoria(), "Accesorios"));
        dto.setCategoryId(product.getCategoryId());
        dto.setMarca(product.getMarca());
        dto.setSku(product.getSku());
        dto.setIsActive(product.getIsActive());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setDestacado(Boolean.TRUE.equals(product.getIsFeatured()));
        dto.setOferta(product.getPrecioOferta() != null && product.getPrecioOferta().compareTo(BigDecimal.ZERO) > 0);
        dto.setTags(safeList(product.getTags()));
        dto.setTagIds(product.getTagIds() == null ? new ArrayList<>() : new ArrayList<>(product.getTagIds()));
        dto.setImages(productImageStorageService.buildPublicImagePaths(product.getImages()));
        
        return dto;
    }

    private List<String> safeList(List<String> values) {
        return values == null ? new ArrayList<>() : new ArrayList<>(values);
    }

    private boolean isSlugInvalid(String slug) {
        if (slug == null || slug.isBlank()) {
            return true;
        }
        return !slug.matches("^[a-z0-9]+(?:-[a-z0-9]+)*$");
    }

    private BigDecimal calculateFinalPrice(Product product) {
        BigDecimal price = product.getPrecioNormal();
        if (price == null) {
            return null;
        }
        Integer discount = product.getDiscountPercentage();
        if (discount == null || discount <= 0) {
            return price;
        }
        BigDecimal factor = BigDecimal.valueOf(100 - discount).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return price.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    private void validateSku(String sku) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("El SKU es obligatorio");
        }
        if (productRepository.existsBySku(sku)) {
            throw new RuntimeException("Ya existe un producto con este SKU");
        }
    }

    private void validateSkuOnUpdate(String newSku, String currentSku) {
        if (newSku == null || newSku.isBlank()) {
            return;
        }
        if (!newSku.equalsIgnoreCase(currentSku) && productRepository.existsBySku(newSku)) {
            throw new RuntimeException("Ya existe un producto con este SKU");
        }
    }

    private String resolveSlug(String provided, String nombre, String fallback) {
        String base = provided;
        if (base == null || base.isBlank()) {
            base = (nombre != null ? nombre : fallback);
        }
        if (base == null) {
            return null;
        }
        return base.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
    }

    private String defaultCategoria(String value, String current) {
        if (value != null && !value.isBlank()) {
            return value;
        }
        if (current != null && !current.isBlank()) {
            return current;
        }
        return "Accesorios";
    }

    private String fallbackDescripcionCorta(Product product) {
        if (product.getDescripcionCorta() != null && !product.getDescripcionCorta().isBlank()) {
            return product.getDescripcionCorta();
        }
        return product.getNombre();
    }
}

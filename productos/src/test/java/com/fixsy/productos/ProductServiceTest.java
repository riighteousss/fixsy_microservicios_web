package com.fixsy.productos;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fixsy.productos.dto.ProductDTO;
import com.fixsy.productos.dto.ProductRequestDTO;
import com.fixsy.productos.model.Product;
import com.fixsy.productos.repository.ProductRepository;
import com.fixsy.productos.service.ProductService;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductRequestDTO testProductRequest;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setNombre("Filtro de aceite");
        testProduct.setDescripcion("Filtro de aceite estándar");
        testProduct.setPrecio(new BigDecimal("9990"));
        testProduct.setPrecioOferta(new BigDecimal("7990"));
        testProduct.setStock(10);
        testProduct.setCategoria("Filtros");
        testProduct.setMarca("Bosch");
        testProduct.setSku("FLT-001");
        testProduct.setIsFeatured(false);
        testProduct.setIsActive(true);
        testProduct.setTags("motor,mantenimiento");
        testProduct.setCreatedAt(LocalDateTime.now());

        testProductRequest = new ProductRequestDTO();
        testProductRequest.setNombre("Filtro de aceite");
        testProductRequest.setDescripcion("Filtro de aceite estándar");
        testProductRequest.setPrecio(new BigDecimal("9990"));
        testProductRequest.setStock(10);
        testProductRequest.setCategoria("Filtros");
        testProductRequest.setMarca("Bosch");
        testProductRequest.setSku("FLT-001");
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(testProduct));

        List<ProductDTO> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Filtro de aceite", result.get(0).getNombre());
        verify(productRepository).findByIsActiveTrue();
    }

    @Test
    void testGetAllProductsIncludeInactive() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct));

        List<ProductDTO> result = productService.getAllProductsIncludeInactive();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository).findAll();
    }

    @Test
    void testGetProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        ProductDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Filtro de aceite", result.getNombre());
    }

    @Test
    void testGetProductByIdNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.getProductById(99L));
    }

    @Test
    void testGetProductBySku() {
        when(productRepository.findBySku("FLT-001")).thenReturn(Optional.of(testProduct));

        ProductDTO result = productService.getProductBySku("FLT-001");

        assertNotNull(result);
        assertEquals("FLT-001", result.getSku());
    }

    @Test
    void testGetProductBySkuNotFound() {
        when(productRepository.findBySku("INVALID")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.getProductBySku("INVALID"));
    }

    @Test
    void testGetFeaturedProducts() {
        testProduct.setIsFeatured(true);
        when(productRepository.findByIsFeaturedTrueAndIsActiveTrue()).thenReturn(Arrays.asList(testProduct));

        List<ProductDTO> result = productService.getFeaturedProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsFeatured());
    }

    @Test
    void testGetProductsOnSale() {
        when(productRepository.findProductsOnSale()).thenReturn(Arrays.asList(testProduct));

        List<ProductDTO> result = productService.getProductsOnSale();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getPrecioOferta());
    }

    @Test
    void testGetProductsByCategory() {
        when(productRepository.findByCategoriaAndIsActiveTrue("Filtros")).thenReturn(Arrays.asList(testProduct));

        List<ProductDTO> result = productService.getProductsByCategory("Filtros");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Filtros", result.get(0).getCategoria());
    }

    @Test
    void testGetProductsByMarca() {
        when(productRepository.findByMarcaAndIsActiveTrue("Bosch")).thenReturn(Arrays.asList(testProduct));

        List<ProductDTO> result = productService.getProductsByMarca("Bosch");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Bosch", result.get(0).getMarca());
    }

    @Test
    void testGetProductsByTag() {
        when(productRepository.findByTag("motor")).thenReturn(Arrays.asList(testProduct));

        List<ProductDTO> result = productService.getProductsByTag("motor");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testSearchProducts() {
        when(productRepository.searchByNombre("Filtro")).thenReturn(Arrays.asList(testProduct));

        List<ProductDTO> result = productService.searchProducts("Filtro");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetProductsInStock() {
        when(productRepository.findByStockGreaterThanAndIsActiveTrue(0)).thenReturn(Arrays.asList(testProduct));

        List<ProductDTO> result = productService.getProductsInStock();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getStock() > 0);
    }

    @Test
    void testGetProductsOutOfStock() {
        testProduct.setStock(0);
        when(productRepository.findByStockLessThanEqualAndIsActiveTrue(0)).thenReturn(Arrays.asList(testProduct));

        List<ProductDTO> result = productService.getProductsOutOfStock();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllCategorias() {
        when(productRepository.findAllCategorias()).thenReturn(Arrays.asList("Filtros", "Aceites", "Frenos"));

        List<String> result = productService.getAllCategorias();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testGetAllMarcas() {
        when(productRepository.findAllMarcas()).thenReturn(Arrays.asList("Bosch", "NGK", "Mann"));

        List<String> result = productService.getAllMarcas();

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testCreateProduct() {
        when(productRepository.existsBySku("FLT-001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        ProductDTO result = productService.createProduct(testProductRequest);

        assertNotNull(result);
        assertEquals("Filtro de aceite", result.getNombre());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testCreateProductDuplicateSku() {
        when(productRepository.existsBySku("FLT-001")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> productService.createProduct(testProductRequest));
    }

    @Test
    void testUpdateProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        ProductDTO result = productService.updateProduct(1L, testProductRequest);

        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testUpdateProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.updateProduct(99L, testProductRequest));
    }

    @Test
    void testUpdateProductDuplicateSku() {
        testProductRequest.setSku("NEW-SKU");
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.existsBySku("NEW-SKU")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> productService.updateProduct(1L, testProductRequest));
    }

    @Test
    void testUpdateStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        ProductDTO result = productService.updateStock(1L, 20);

        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testUpdateStockNegative() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        assertThrows(RuntimeException.class, () -> productService.updateStock(1L, -5));
    }

    @Test
    void testAdjustStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        ProductDTO result = productService.adjustStock(1L, 5);

        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testAdjustStockInsufficientStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        assertThrows(RuntimeException.class, () -> productService.adjustStock(1L, -100));
    }

    @Test
    void testToggleFeatured() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        ProductDTO result = productService.toggleFeatured(1L);

        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testToggleActive() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        ProductDTO result = productService.toggleActive(1L);

        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testDeleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    void testDeleteProductNotFound() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> productService.deleteProduct(99L));
    }
}


package com.fixsy.productos.service;

import com.fixsy.productos.dto.ProductDTO;
import com.fixsy.productos.dto.ProductRequestDTO;
import com.fixsy.productos.model.Product;
import com.fixsy.productos.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

class ProductServiceUpdateTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductImageStorageService productImageStorageService;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        given(productImageStorageService.buildPublicImagePath(any())).willAnswer(inv -> inv.getArgument(0));
        given(productImageStorageService.buildPublicImagePaths(anyList())).willAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void updateProduct_shouldUpdateFields() {
        Product existing = baseProduct();
        given(productRepository.findById(1L)).willReturn(Optional.of(existing));
        given(productRepository.save(any(Product.class))).willAnswer(inv -> inv.getArgument(0));

        ProductRequestDTO request = new ProductRequestDTO();
        request.setNombre("Nuevo");
        request.setPrecioNormal(new BigDecimal("15000"));
        request.setPrecioOferta(new BigDecimal("12000"));
        request.setStock(5);
        request.setSku("SKU-1");

        ProductDTO dto = productService.updateProduct(1L, request);

        assertEquals("Nuevo", dto.getNombre());
        assertEquals(new BigDecimal("15000"), dto.getPrecioNormal());
        assertNull(dto.getPrecioOferta()); // la oferta se reinicia en updateProduct
        assertEquals(5, dto.getStock());
    }

    @Test
    void updatePrice_shouldSetNewPriceAndClearInvalidOffer() {
        Product existing = baseProduct();
        existing.setPrecioNormal(new BigDecimal("10000"));
        existing.setPrecioOferta(new BigDecimal("9000"));
        given(productRepository.findById(1L)).willReturn(Optional.of(existing));
        given(productRepository.save(any(Product.class))).willAnswer(inv -> inv.getArgument(0));

        ProductDTO dto = productService.updatePrice(1L, new BigDecimal("8000"));

        assertEquals(new BigDecimal("8000"), dto.getPrecioNormal());
        assertNull(dto.getPrecioOferta()); // oferta eliminada por ser mayor al nuevo precio
    }

    @Test
    void updateOffer_shouldSetAndRemoveOffer() {
        Product existing = baseProduct();
        existing.setPrecioNormal(new BigDecimal("10000"));
        given(productRepository.findById(1L)).willReturn(Optional.of(existing));
        given(productRepository.save(any(Product.class))).willAnswer(inv -> inv.getArgument(0));

        ProductDTO dto = productService.updateOffer(1L, new BigDecimal("8000"));
        assertEquals(new BigDecimal("8000"), dto.getPrecioOferta());

        ProductDTO dto2 = productService.updateOffer(1L, new BigDecimal("0"));
        assertNull(dto2.getPrecioOferta());
    }

    private Product baseProduct() {
        Product p = new Product();
        p.setId(1L);
        p.setNombre("Prod");
        p.setPrecioNormal(new BigDecimal("10000"));
        p.setStock(10);
        p.setIsActive(true);
        p.setIsFeatured(false);
        return p;
    }
}

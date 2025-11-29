package com.fixsy.productos;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fixsy.productos.controller.ProductController;
import com.fixsy.productos.dto.ProductDTO;
import com.fixsy.productos.dto.ProductRequestDTO;
import com.fixsy.productos.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private ObjectMapper objectMapper;
    private ProductDTO testProductDTO;
    private ProductRequestDTO testProductRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testProductDTO = new ProductDTO();
        testProductDTO.setId(1L);
        testProductDTO.setNombre("Filtro de aceite");
        testProductDTO.setDescripcion("Filtro de aceite estándar");
        testProductDTO.setPrecio(new BigDecimal("9990"));
        testProductDTO.setPrecioOferta(new BigDecimal("7990"));
        testProductDTO.setStock(10);
        testProductDTO.setCategoria("Filtros");
        testProductDTO.setMarca("Bosch");
        testProductDTO.setSku("FLT-001");
        testProductDTO.setIsFeatured(false);
        testProductDTO.setIsActive(true);
        testProductDTO.setTags(Arrays.asList("motor", "mantenimiento"));
        testProductDTO.setImages(Collections.emptyList());
        testProductDTO.setCreatedAt(LocalDateTime.now());

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
    void testGetAllProducts() throws Exception {
        when(productService.getAllProducts()).thenReturn(Arrays.asList(testProductDTO));

        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].nombre").value("Filtro de aceite"));
    }

    @Test
    void testGetAllProductsIncludeInactive() throws Exception {
        when(productService.getAllProductsIncludeInactive()).thenReturn(Arrays.asList(testProductDTO));

        mockMvc.perform(get("/api/products/all")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetProductById() throws Exception {
        when(productService.getProductById(1L)).thenReturn(testProductDTO);

        mockMvc.perform(get("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.nombre").value("Filtro de aceite"));
    }

    @Test
    void testGetProductBySku() throws Exception {
        when(productService.getProductBySku("FLT-001")).thenReturn(testProductDTO);

        mockMvc.perform(get("/api/products/sku/FLT-001")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sku").value("FLT-001"));
    }

    @Test
    void testGetFeaturedProducts() throws Exception {
        testProductDTO.setIsFeatured(true);
        when(productService.getFeaturedProducts()).thenReturn(Arrays.asList(testProductDTO));

        mockMvc.perform(get("/api/products/featured")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].isFeatured").value(true));
    }

    @Test
    void testGetProductsOnSale() throws Exception {
        when(productService.getProductsOnSale()).thenReturn(Arrays.asList(testProductDTO));

        mockMvc.perform(get("/api/products/on-sale")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetProductsByCategory() throws Exception {
        when(productService.getProductsByCategory("Filtros")).thenReturn(Arrays.asList(testProductDTO));

        mockMvc.perform(get("/api/products/category/Filtros")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].categoria").value("Filtros"));
    }

    @Test
    void testGetProductsByMarca() throws Exception {
        when(productService.getProductsByMarca("Bosch")).thenReturn(Arrays.asList(testProductDTO));

        mockMvc.perform(get("/api/products/marca/Bosch")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].marca").value("Bosch"));
    }

    @Test
    void testGetProductsByTag() throws Exception {
        when(productService.getProductsByTag("motor")).thenReturn(Arrays.asList(testProductDTO));

        mockMvc.perform(get("/api/products/tag/motor")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testSearchProducts() throws Exception {
        when(productService.searchProducts("Filtro")).thenReturn(Arrays.asList(testProductDTO));

        mockMvc.perform(get("/api/products/search")
                .param("q", "Filtro")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetProductsInStock() throws Exception {
        when(productService.getProductsInStock()).thenReturn(Arrays.asList(testProductDTO));

        mockMvc.perform(get("/api/products/in-stock")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetProductsOutOfStock() throws Exception {
        testProductDTO.setStock(0);
        when(productService.getProductsOutOfStock()).thenReturn(Arrays.asList(testProductDTO));

        mockMvc.perform(get("/api/products/out-of-stock")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetAllCategorias() throws Exception {
        when(productService.getAllCategorias()).thenReturn(Arrays.asList("Filtros", "Aceites", "Frenos"));

        mockMvc.perform(get("/api/products/categorias")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void testGetAllMarcas() throws Exception {
        when(productService.getAllMarcas()).thenReturn(Arrays.asList("Bosch", "NGK", "Mann"));

        mockMvc.perform(get("/api/products/marcas")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void testCreateProduct() throws Exception {
        when(productService.createProduct(Mockito.any(ProductRequestDTO.class))).thenReturn(testProductDTO);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nombre").value("Filtro de aceite"));
    }

    @Test
    void testUpdateProduct() throws Exception {
        when(productService.updateProduct(eq(1L), Mockito.any(ProductRequestDTO.class))).thenReturn(testProductDTO);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Filtro de aceite"));
    }

    @Test
    void testUpdateStock() throws Exception {
        when(productService.updateStock(1L, 20)).thenReturn(testProductDTO);

        mockMvc.perform(put("/api/products/1/stock")
                .param("stock", "20")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void testAdjustStock() throws Exception {
        when(productService.adjustStock(1L, 5)).thenReturn(testProductDTO);

        mockMvc.perform(put("/api/products/1/stock/adjust")
                .param("adjustment", "5")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void testToggleFeatured() throws Exception {
        when(productService.toggleFeatured(1L)).thenReturn(testProductDTO);

        mockMvc.perform(put("/api/products/1/featured")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void testToggleActive() throws Exception {
        when(productService.toggleActive(1L)).thenReturn(testProductDTO);

        mockMvc.perform(put("/api/products/1/active")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void testDeleteProduct() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        verify(productService).deleteProduct(1L);
    }
}


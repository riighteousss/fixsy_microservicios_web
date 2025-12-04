package com.fixsy.productos.controller;

import com.fixsy.productos.dto.ProductDTO;
import com.fixsy.productos.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false) // desactiva filtros de seguridad para el test
@SuppressWarnings("removal")
class ProductControllerUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private com.fixsy.productos.repository.ProductRepository productRepository;

    @MockBean
    private com.fixsy.productos.service.ProductImageStorageService productImageStorageService;

    @Test
    @WithMockUser(roles = "ADMIN") // Simula un usuario con rol ADMIN
    void updateProduct_shouldReturn200() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setId(1L);
        dto.setNombre("Nuevo");
        dto.setPrecioNormal(new BigDecimal("15000"));
        given(productService.updateProduct(eq(1L), any(), any())).willReturn(dto);

        String body = """
                {
                  "nombre": "Nuevo",
                  "precioNormal": 15000,
                  "stock": 5
                }
                """;

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Nuevo")))
                .andExpect(jsonPath("$.precioNormal", is(15000)));
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Simula un usuario con rol ADMIN
    void updatePrice_shouldReturn200() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setId(2L);
        dto.setPrecioNormal(new BigDecimal("9000"));
        given(productService.updatePrice(eq(2L), eq(new BigDecimal("9000")))).willReturn(dto);

        String body = """
                {
                  "precioNormal": 9000
                }
                """;

        mockMvc.perform(patch("/api/products/2/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precioNormal", is(9000)));
    }

    @Test
    @WithMockUser(roles = "ADMIN") // Simula un usuario con rol ADMIN
    void updateOffer_shouldReturn200() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setId(3L);
        dto.setPrecioNormal(new BigDecimal("12000"));
        dto.setPrecioOferta(new BigDecimal("10000"));
        given(productService.updateOffer(eq(3L), eq(new BigDecimal("10000")))).willReturn(dto);

        String body = """
                {
                  "precioOferta": 10000
                }
                """;

        mockMvc.perform(patch("/api/products/3/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precioOferta", is(10000)));
    }
}

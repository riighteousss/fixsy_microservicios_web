package com.fixsy.ordenes.controller;

import com.fixsy.ordenes.dto.OrderDTO;
import com.fixsy.ordenes.dto.OrderItemDTO;
import com.fixsy.ordenes.dto.OrderItemRequestDTO;
import com.fixsy.ordenes.dto.OrderRequestDTO;
import com.fixsy.ordenes.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false) // desactiva filtros de seguridad para las pruebas
@SuppressWarnings("removal")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createOrder_shouldReturn201WithBody() throws Exception {
        OrderDTO dto = new OrderDTO();
        dto.setId(1L);
        dto.setStatus("Pendiente");
        dto.setTotal(new BigDecimal("10000"));
        dto.setItems(Collections.singletonList(new OrderItemDTO(1L, 2L, "Prod", "SKU", 1, new BigDecimal("12000"), new BigDecimal("10000"), new BigDecimal("2000"), new BigDecimal("10000"))));
        given(orderService.createOrder(any())).willReturn(dto);

        OrderRequestDTO request = new OrderRequestDTO();
        request.setUserId(1L);
        request.setUserEmail("user@example.com");
        request.setUserName("User");
        request.setShippingAddress("Calle 1");
        request.setShippingRegion("Metropolitana");
        request.setItems(Collections.singletonList(new OrderItemRequestDTO(1L, "Prod", "SKU", 1, new BigDecimal("12000"), new BigDecimal("10000"), new BigDecimal("10000"))));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("Pendiente")))
                .andExpect(jsonPath("$.total", is(10000)));
    }

    @Test
    void createOrder_shouldReturn400OnValidationError() throws Exception {
        given(orderService.createOrder(any())).willThrow(new IllegalArgumentException("La orden debe tener al menos un item"));

        OrderRequestDTO request = new OrderRequestDTO();
        request.setUserId(1L);
        request.setUserEmail("user@example.com");
        request.setUserName("User");
        request.setShippingAddress("Calle 1");
        request.setShippingRegion("Metropolitana");
        request.setItems(Collections.emptyList());

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOrderById_shouldReturn200WhenExists() throws Exception {
        OrderDTO dto = new OrderDTO();
        dto.setId(2L);
        dto.setStatus("Pendiente");
        given(orderService.getOrderById(2L)).willReturn(dto);

        mockMvc.perform(get("/api/orders/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.status", is("Pendiente")));
    }

    @Test
    void getOrderById_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new RuntimeException("Orden no encontrada")).when(orderService).getOrderById(99L);

        mockMvc.perform(get("/api/orders/99"))
                .andExpect(status().isNotFound());
    }
}

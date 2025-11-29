package com.fixsy.ordenes;

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

import com.fixsy.ordenes.controller.OrderController;
import com.fixsy.ordenes.dto.OrderDTO;
import com.fixsy.ordenes.dto.OrderItemDTO;
import com.fixsy.ordenes.dto.OrderItemRequestDTO;
import com.fixsy.ordenes.dto.OrderRequestDTO;
import com.fixsy.ordenes.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    private ObjectMapper objectMapper;
    private OrderDTO testOrderDTO;
    private OrderRequestDTO testOrderRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        OrderItemDTO itemDTO = new OrderItemDTO(
            1L, 1L, "Filtro de aceite", "FLT-001", 
            2, new BigDecimal("9990"), new BigDecimal("19980")
        );

        testOrderDTO = new OrderDTO();
        testOrderDTO.setId(1L);
        testOrderDTO.setUserId(1L);
        testOrderDTO.setUserEmail("cliente@example.com");
        testOrderDTO.setUserName("Juan Pérez");
        testOrderDTO.setStatus("Pendiente");
        testOrderDTO.setSubtotal(new BigDecimal("19980"));
        testOrderDTO.setShippingCost(new BigDecimal("3990"));
        testOrderDTO.setTotal(new BigDecimal("23970"));
        testOrderDTO.setShippingAddress("Av. Principal 123");
        testOrderDTO.setShippingRegion("Metropolitana");
        testOrderDTO.setContactPhone("+56912345678");
        testOrderDTO.setPaymentMethod("Transferencia");
        testOrderDTO.setItems(Arrays.asList(itemDTO));
        testOrderDTO.setCreatedAt(LocalDateTime.now());

        OrderItemRequestDTO itemRequest = new OrderItemRequestDTO();
        itemRequest.setProductId(1L);
        itemRequest.setProductName("Filtro de aceite");
        itemRequest.setProductSku("FLT-001");
        itemRequest.setQuantity(2);
        itemRequest.setUnitPrice(new BigDecimal("9990"));

        testOrderRequest = new OrderRequestDTO();
        testOrderRequest.setUserId(1L);
        testOrderRequest.setUserEmail("cliente@example.com");
        testOrderRequest.setUserName("Juan Pérez");
        testOrderRequest.setItems(Arrays.asList(itemRequest));
        testOrderRequest.setShippingAddress("Av. Principal 123");
        testOrderRequest.setShippingRegion("Metropolitana");
        testOrderRequest.setContactPhone("+56912345678");
        testOrderRequest.setPaymentMethod("Transferencia");
    }

    @Test
    void testGetAllOrders() throws Exception {
        when(orderService.getAllOrders()).thenReturn(Arrays.asList(testOrderDTO));

        mockMvc.perform(get("/api/orders")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].userEmail").value("cliente@example.com"));
    }

    @Test
    void testGetOrderById() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(testOrderDTO);

        mockMvc.perform(get("/api/orders/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.userEmail").value("cliente@example.com"));
    }

    @Test
    void testGetOrdersByUserId() throws Exception {
        when(orderService.getOrdersByUserId(1L)).thenReturn(Arrays.asList(testOrderDTO));

        mockMvc.perform(get("/api/orders/user/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetOrdersByUserEmail() throws Exception {
        when(orderService.getOrdersByUserEmail("cliente@example.com"))
            .thenReturn(Arrays.asList(testOrderDTO));

        mockMvc.perform(get("/api/orders/email/cliente@example.com")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetOrdersByStatus() throws Exception {
        when(orderService.getOrdersByStatus("Pendiente")).thenReturn(Arrays.asList(testOrderDTO));

        mockMvc.perform(get("/api/orders/status/Pendiente")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].status").value("Pendiente"));
    }

    @Test
    void testGetOrdersPendingShipment() throws Exception {
        testOrderDTO.setStatus("Pagado");
        when(orderService.getOrdersPendingShipment()).thenReturn(Arrays.asList(testOrderDTO));

        mockMvc.perform(get("/api/orders/pending-shipment")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetOrderByTrackingNumber() throws Exception {
        testOrderDTO.setTrackingNumber("TRACK-123");
        when(orderService.getOrderByTrackingNumber("TRACK-123")).thenReturn(testOrderDTO);

        mockMvc.perform(get("/api/orders/tracking/TRACK-123")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.trackingNumber").value("TRACK-123"));
    }

    @Test
    void testCreateOrder() throws Exception {
        when(orderService.createOrder(Mockito.any(OrderRequestDTO.class))).thenReturn(testOrderDTO);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testOrderRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userEmail").value("cliente@example.com"));
    }

    @Test
    void testUpdateOrderStatus() throws Exception {
        when(orderService.updateOrderStatus(1L, "Pagado")).thenReturn(testOrderDTO);

        mockMvc.perform(put("/api/orders/1/status")
                .param("status", "Pagado")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void testUpdateTrackingNumber() throws Exception {
        when(orderService.updateTrackingNumber(1L, "TRACK-456")).thenReturn(testOrderDTO);

        mockMvc.perform(put("/api/orders/1/tracking")
                .param("trackingNumber", "TRACK-456")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void testUpdatePaymentReference() throws Exception {
        when(orderService.updatePaymentReference(1L, "PAY-REF-123")).thenReturn(testOrderDTO);

        mockMvc.perform(put("/api/orders/1/payment")
                .param("paymentReference", "PAY-REF-123")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void testAddAdminNote() throws Exception {
        when(orderService.addAdminNote(1L, "Nota de prueba")).thenReturn(testOrderDTO);

        mockMvc.perform(put("/api/orders/1/admin-note")
                .param("note", "Nota de prueba")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void testDeleteOrder() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/api/orders/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        verify(orderService).deleteOrder(1L);
    }

    @Test
    void testCountByStatus() throws Exception {
        when(orderService.countOrdersByStatus("Pendiente")).thenReturn(5L);

        mockMvc.perform(get("/api/orders/stats/count/Pendiente")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("Pendiente"))
            .andExpect(jsonPath("$.count").value(5));
    }

    @Test
    void testCountByUser() throws Exception {
        when(orderService.countOrdersByUser(1L)).thenReturn(3L);

        mockMvc.perform(get("/api/orders/stats/user/1/count")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.count").value(3));
    }

    @Test
    void testGetTotalSales() throws Exception {
        when(orderService.getTotalSales()).thenReturn(new BigDecimal("100000"));

        mockMvc.perform(get("/api/orders/stats/total-sales")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalSales").value(100000));
    }
}


package com.fixsy.ordenes.service;

import com.fixsy.ordenes.dto.OrderDTO;
import com.fixsy.ordenes.dto.OrderItemRequestDTO;
import com.fixsy.ordenes.dto.OrderRequestDTO;
import com.fixsy.ordenes.model.Order;
import com.fixsy.ordenes.model.OrderItem;
import com.fixsy.ordenes.repository.OrderItemRepository;
import com.fixsy.ordenes.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_success_calculatesTotalsAndStatusPending() {
        OrderRequestDTO request = new OrderRequestDTO();
        request.setUserId(1L);
        request.setUserEmail("user@example.com");
        request.setUserName("User");
        request.setShippingRegion("Metropolitana");
        request.setShippingAddress("Calle 123");

        OrderItemRequestDTO item1 = new OrderItemRequestDTO(1L, "Producto1", "SKU1", 2, new BigDecimal("1000"), null, new BigDecimal("1000"));
        OrderItemRequestDTO item2 = new OrderItemRequestDTO(2L, "Producto2", "SKU2", 1, new BigDecimal("500"), null, new BigDecimal("500"));
        request.setItems(List.of(item1, item2));

        Order saved = new Order();
        saved.setId(10L);
        saved.setCreatedAt(LocalDateTime.now());
        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(10L);
            return o;
        });
        given(orderItemRepository.save(any(OrderItem.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(orderItemRepository.findByOrder_Id(10L)).willReturn(Collections.emptyList());

        OrderDTO result = orderService.createOrder(request);

        // subtotal = 2*1000 + 1*500 = 2500
        assertEquals(new BigDecimal("2500"), result.getSubtotal());
        // shipping Metropolitana = 3990
        assertEquals(new BigDecimal("3990"), result.getShippingCost());
        assertEquals(new BigDecimal("6490"), result.getTotal());
        assertEquals("Pendiente", result.getStatus());

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals("Pendiente", orderCaptor.getValue().getStatus());

        verify(orderItemRepository, org.mockito.Mockito.times(2)).save(any(OrderItem.class));
    }

    @Test
    void createOrder_invalidItems_throwsException() {
        OrderRequestDTO request = new OrderRequestDTO();
        request.setUserId(1L);
        request.setUserEmail("user@example.com");
        request.setUserName("User");
        request.setShippingRegion("Metropolitana");
        request.setItems(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(request));
    }

    @Test
    void updateStatus_setsTimestampAndStatus() {
        Order existing = new Order();
        existing.setId(5L);
        existing.setStatus("Pendiente");
        given(orderRepository.findById(5L)).willReturn(Optional.of(existing));
        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(orderItemRepository.findByOrder_Id(5L)).willReturn(Collections.emptyList());

        OrderDTO result = orderService.updateOrderStatus(5L, "Enviado");

        assertEquals("Enviado", result.getStatus());
        assertNotNull(result.getShippedAt());
    }
}

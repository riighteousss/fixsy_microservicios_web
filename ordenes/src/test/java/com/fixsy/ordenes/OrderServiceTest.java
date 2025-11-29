package com.fixsy.ordenes;

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

import com.fixsy.ordenes.dto.OrderDTO;
import com.fixsy.ordenes.dto.OrderItemRequestDTO;
import com.fixsy.ordenes.dto.OrderRequestDTO;
import com.fixsy.ordenes.model.Order;
import com.fixsy.ordenes.model.OrderItem;
import com.fixsy.ordenes.repository.OrderItemRepository;
import com.fixsy.ordenes.repository.OrderRepository;
import com.fixsy.ordenes.service.OrderService;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    private OrderItem testOrderItem;
    private OrderRequestDTO testOrderRequest;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUserId(1L);
        testOrder.setUserEmail("cliente@example.com");
        testOrder.setUserName("Juan Pérez");
        testOrder.setStatus("Pendiente");
        testOrder.setSubtotal(new BigDecimal("29990"));
        testOrder.setShippingCost(new BigDecimal("3990"));
        testOrder.setTotal(new BigDecimal("33980"));
        testOrder.setShippingAddress("Av. Principal 123");
        testOrder.setShippingRegion("Metropolitana");
        testOrder.setContactPhone("+56912345678");
        testOrder.setPaymentMethod("Transferencia");
        testOrder.setCreatedAt(LocalDateTime.now());

        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setOrderId(1L);
        testOrderItem.setProductId(1L);
        testOrderItem.setProductName("Filtro de aceite");
        testOrderItem.setProductSku("FLT-001");
        testOrderItem.setQuantity(2);
        testOrderItem.setUnitPrice(new BigDecimal("9990"));
        testOrderItem.setSubtotal(new BigDecimal("19980"));

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
    void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(Arrays.asList(testOrder));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Arrays.asList(testOrderItem));

        List<OrderDTO> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    void testGetOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Arrays.asList(testOrderItem));

        OrderDTO result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("cliente@example.com", result.getUserEmail());
    }

    @Test
    void testGetOrderByIdNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.getOrderById(99L));
    }

    @Test
    void testGetOrdersByUserId() {
        when(orderRepository.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(Arrays.asList(testOrder));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Arrays.asList(testOrderItem));

        List<OrderDTO> result = orderService.getOrdersByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetOrdersByUserEmail() {
        when(orderRepository.findByUserEmailOrderByCreatedAtDesc("cliente@example.com"))
            .thenReturn(Arrays.asList(testOrder));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Arrays.asList(testOrderItem));

        List<OrderDTO> result = orderService.getOrdersByUserEmail("cliente@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetOrdersByStatus() {
        when(orderRepository.findByStatusOrderByCreatedAtDesc("Pendiente"))
            .thenReturn(Arrays.asList(testOrder));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Arrays.asList(testOrderItem));

        List<OrderDTO> result = orderService.getOrdersByStatus("Pendiente");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetOrdersPendingShipment() {
        testOrder.setStatus("Pagado");
        when(orderRepository.findOrdersPendingShipment()).thenReturn(Arrays.asList(testOrder));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Arrays.asList(testOrderItem));

        List<OrderDTO> result = orderService.getOrdersPendingShipment();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetOrderByTrackingNumber() {
        testOrder.setTrackingNumber("TRACK-123");
        when(orderRepository.findByTrackingNumber("TRACK-123")).thenReturn(Optional.of(testOrder));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Arrays.asList(testOrderItem));

        OrderDTO result = orderService.getOrderByTrackingNumber("TRACK-123");

        assertNotNull(result);
        assertEquals("TRACK-123", result.getTrackingNumber());
    }

    @Test
    void testGetOrderByTrackingNumberNotFound() {
        when(orderRepository.findByTrackingNumber("INVALID")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.getOrderByTrackingNumber("INVALID"));
    }

    @Test
    void testCreateOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Arrays.asList(testOrderItem));

        OrderDTO result = orderService.createOrder(testOrderRequest);

        assertNotNull(result);
        assertEquals("cliente@example.com", result.getUserEmail());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testCreateOrderCalculatesShipping() {
        testOrderRequest.setShippingCost(null);
        testOrderRequest.setShippingRegion("Metropolitana");
        
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Arrays.asList(testOrderItem));

        OrderDTO result = orderService.createOrder(testOrderRequest);

        assertNotNull(result);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testUpdateOrderStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Arrays.asList(testOrderItem));

        OrderDTO result = orderService.updateOrderStatus(1L, "Pagado");

        assertNotNull(result);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testUpdateOrderStatusToShipped() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Arrays.asList(testOrderItem));

        OrderDTO result = orderService.updateOrderStatus(1L, "Enviado");

        assertNotNull(result);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testUpdateOrderStatusToDelivered() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Arrays.asList(testOrderItem));

        OrderDTO result = orderService.updateOrderStatus(1L, "Entregado");

        assertNotNull(result);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testUpdateTrackingNumber() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Arrays.asList(testOrderItem));

        OrderDTO result = orderService.updateTrackingNumber(1L, "TRACK-456");

        assertNotNull(result);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testUpdatePaymentReference() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Arrays.asList(testOrderItem));

        OrderDTO result = orderService.updatePaymentReference(1L, "PAY-REF-123");

        assertNotNull(result);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testAddAdminNote() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Arrays.asList(testOrderItem));

        OrderDTO result = orderService.addAdminNote(1L, "Nota de prueba");

        assertNotNull(result);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testDeleteOrder() {
        when(orderRepository.existsById(1L)).thenReturn(true);
        doNothing().when(orderItemRepository).deleteByOrderId(1L);
        doNothing().when(orderRepository).deleteById(1L);

        orderService.deleteOrder(1L);

        verify(orderItemRepository).deleteByOrderId(1L);
        verify(orderRepository).deleteById(1L);
    }

    @Test
    void testDeleteOrderNotFound() {
        when(orderRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> orderService.deleteOrder(99L));
    }

    @Test
    void testCountOrdersByStatus() {
        when(orderRepository.countByStatus("Pendiente")).thenReturn(5L);

        Long result = orderService.countOrdersByStatus("Pendiente");

        assertEquals(5L, result);
    }

    @Test
    void testCountOrdersByUser() {
        when(orderRepository.countByUserId(1L)).thenReturn(3L);

        Long result = orderService.countOrdersByUser(1L);

        assertEquals(3L, result);
    }

    @Test
    void testGetTotalSales() {
        when(orderRepository.getTotalSales()).thenReturn(new BigDecimal("100000"));

        BigDecimal result = orderService.getTotalSales();

        assertEquals(new BigDecimal("100000"), result);
    }

    @Test
    void testGetTotalSalesNull() {
        when(orderRepository.getTotalSales()).thenReturn(null);

        BigDecimal result = orderService.getTotalSales();

        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testGetTotalSalesForPeriod() {
        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now();
        
        when(orderRepository.getTotalSalesBetween(start, end)).thenReturn(new BigDecimal("50000"));

        BigDecimal result = orderService.getTotalSalesForPeriod(start, end);

        assertEquals(new BigDecimal("50000"), result);
    }
}


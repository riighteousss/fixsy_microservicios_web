package com.fixsy.ordenes.service;

import com.fixsy.ordenes.dto.*;
import com.fixsy.ordenes.model.Order;
import com.fixsy.ordenes.model.OrderItem;
import com.fixsy.ordenes.repository.OrderRepository;
import com.fixsy.ordenes.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        return convertToDTO(order);
    }

    public List<OrderDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByUserEmail(String email) {
        return orderRepository.findByUserEmailOrderByCreatedAtDesc(email).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByStatus(String status) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersPendingShipment() {
        return orderRepository.findOrdersPendingShipment().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderByTrackingNumber(String trackingNumber) {
        Order order = orderRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        return convertToDTO(order);
    }

    @Transactional
    public OrderDTO createOrder(OrderRequestDTO request) {
        // Calcular subtotal
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItemRequestDTO item : request.getItems()) {
            BigDecimal itemSubtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(itemSubtotal);
        }
        
        // Costo de envío (usar el proporcionado o calcular)
        BigDecimal shippingCost = request.getShippingCost() != null ? 
                request.getShippingCost() : calculateShippingCost(request.getShippingRegion());
        
        // Total
        BigDecimal total = subtotal.add(shippingCost);

        // Crear orden
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setUserEmail(request.getUserEmail());
        order.setUserName(request.getUserName());
        order.setStatus("Pendiente");
        order.setSubtotal(subtotal);
        order.setShippingCost(shippingCost);
        order.setTotal(total);
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingRegion(request.getShippingRegion());
        order.setShippingComuna(request.getShippingComuna());
        order.setContactPhone(request.getContactPhone());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNotes(request.getNotes());

        Order savedOrder = orderRepository.save(order);

        // Crear items
        for (OrderItemRequestDTO itemRequest : request.getItems()) {
            OrderItem item = new OrderItem();
            item.setOrderId(savedOrder.getId());
            item.setProductId(itemRequest.getProductId());
            item.setProductName(itemRequest.getProductName());
            item.setProductSku(itemRequest.getProductSku());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setSubtotal(itemRequest.getUnitPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            orderItemRepository.save(item);
        }

        return convertToDTO(savedOrder);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        
        order.setStatus(status);
        
        // Actualizar timestamps según el estado
        LocalDateTime now = LocalDateTime.now();
        switch (status) {
            case "Pagado":
                order.setPaidAt(now);
                break;
            case "Enviado":
                order.setShippedAt(now);
                break;
            case "Entregado":
                order.setDeliveredAt(now);
                break;
        }
        
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Transactional
    public OrderDTO updateTrackingNumber(Long id, String trackingNumber) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        
        order.setTrackingNumber(trackingNumber);
        
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Transactional
    public OrderDTO updatePaymentReference(Long id, String paymentReference) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        
        order.setPaymentReference(paymentReference);
        order.setStatus("Pagado");
        order.setPaidAt(LocalDateTime.now());
        
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Transactional
    public OrderDTO addAdminNote(Long id, String note) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        
        String currentNotes = order.getAdminNotes() != null ? order.getAdminNotes() : "";
        String timestamp = LocalDateTime.now().toString();
        order.setAdminNotes(currentNotes + "\n[" + timestamp + "] " + note);
        
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Orden no encontrada");
        }
        orderItemRepository.deleteByOrderId(id);
        orderRepository.deleteById(id);
    }

    // Estadísticas
    public Long countOrdersByStatus(String status) {
        return orderRepository.countByStatus(status);
    }

    public Long countOrdersByUser(Long userId) {
        return orderRepository.countByUserId(userId);
    }

    public BigDecimal getTotalSales() {
        BigDecimal total = orderRepository.getTotalSales();
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalSalesForPeriod(LocalDateTime start, LocalDateTime end) {
        BigDecimal total = orderRepository.getTotalSalesBetween(start, end);
        return total != null ? total : BigDecimal.ZERO;
    }

    private BigDecimal calculateShippingCost(String region) {
        // Lógica básica de costo de envío por región
        if (region == null) return new BigDecimal("5990");
        
        switch (region.toLowerCase()) {
            case "metropolitana":
                return new BigDecimal("3990");
            case "valparaíso":
            case "o'higgins":
                return new BigDecimal("4990");
            default:
                return new BigDecimal("5990");
        }
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setUserEmail(order.getUserEmail());
        dto.setUserName(order.getUserName());
        dto.setStatus(order.getStatus());
        dto.setSubtotal(order.getSubtotal());
        dto.setShippingCost(order.getShippingCost());
        dto.setTotal(order.getTotal());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setShippingRegion(order.getShippingRegion());
        dto.setShippingComuna(order.getShippingComuna());
        dto.setContactPhone(order.getContactPhone());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentReference(order.getPaymentReference());
        dto.setNotes(order.getNotes());
        dto.setTrackingNumber(order.getTrackingNumber());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setPaidAt(order.getPaidAt());
        dto.setShippedAt(order.getShippedAt());
        dto.setDeliveredAt(order.getDeliveredAt());
        
        // Cargar items
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        dto.setItems(items.stream().map(this::convertItemToDTO).collect(Collectors.toList()));
        
        return dto;
    }

    private OrderItemDTO convertItemToDTO(OrderItem item) {
        return new OrderItemDTO(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getProductSku(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }
}


package com.fixsy.ordenes.service;

import com.fixsy.ordenes.dto.OrderDTO;
import com.fixsy.ordenes.dto.OrderItemDTO;
import com.fixsy.ordenes.dto.OrderItemRequestDTO;
import com.fixsy.ordenes.dto.OrderRequestDTO;
import com.fixsy.ordenes.model.Order;
import com.fixsy.ordenes.model.OrderItem;
import com.fixsy.ordenes.repository.OrderItemRepository;
import com.fixsy.ordenes.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private static final BigDecimal IVA_RATE = new BigDecimal("0.19");

    public OrderDTO createPublicOrder(OrderRequestDTO request) {
        request.setUserId(0L);
        return createOrder(request);
    }

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
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("La orden debe tener al menos un item");
        }

        if (request.getUserEmail() == null || request.getUserEmail().isBlank()) {
            throw new IllegalArgumentException("El email del comprador es obligatorio");
        }
        if (request.getUserName() == null || request.getUserName().isBlank()) {
            throw new IllegalArgumentException("El nombre del comprador es obligatorio");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItemRequestDTO item : request.getItems()) {
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("La cantidad del item debe ser mayor a 0");
            }
            // Fallback: si no viene SKU pero sí productId, generar uno automático
            if (item.getProductSku() == null || item.getProductSku().isBlank()) {
                if (item.getProductId() != null) {
                    item.setProductSku("PROD-" + item.getProductId());
                } else {
                    throw new IllegalArgumentException("El SKU del producto es obligatorio");
                }
            }

            BigDecimal original = item.getPrecio() != null ? item.getPrecio() : item.getUnitPrice();
            BigDecimal effective = item.getPrecioOferta() != null && item.getPrecioOferta().compareTo(BigDecimal.ZERO) > 0
                    ? item.getPrecioOferta()
                    : original;
            if (original == null) {
                original = effective;
            }
            if (effective == null) {
                effective = original;
            }
            if (original == null) {
                throw new IllegalArgumentException("Precio del item no puede ser nulo");
            }
            if (effective.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("El precio del item no puede ser negativo");
            }
            BigDecimal itemSubtotal = effective.multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(itemSubtotal);
        }

        BigDecimal shippingCost = request.getShippingCost() != null
                ? request.getShippingCost()
                : calculateShippingCost(request.getShippingRegion(), request.getShippingAddress());

        BigDecimal ivaAmount = calculateIva(subtotal);
        BigDecimal total = subtotal.add(shippingCost).add(ivaAmount);

        Order order = new Order();
        Long resolvedUserId = request.getUserId();
        if (resolvedUserId == null || resolvedUserId <= 0) {
            resolvedUserId = 0L;
            if (request.getPaymentMethod() == null || request.getPaymentMethod().isBlank()) {
                throw new IllegalArgumentException("El metodo de pago es obligatorio para invitados");
            }
        }
        order.setUserId(resolvedUserId);
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

        // Crear items y asociarlos a la orden usando addItem() para relación bidireccional
        for (OrderItemRequestDTO itemRequest : request.getItems()) {
            BigDecimal original = itemRequest.getPrecio() != null ? itemRequest.getPrecio() : itemRequest.getUnitPrice();
            BigDecimal effective = itemRequest.getPrecioOferta() != null && itemRequest.getPrecioOferta().compareTo(BigDecimal.ZERO) > 0
                    ? itemRequest.getPrecioOferta()
                    : original;
            if (original == null) {
                original = effective;
            }
            if (effective == null) {
                effective = original;
            }
            BigDecimal discount = original.subtract(effective);
            if (discount.compareTo(BigDecimal.ZERO) < 0) {
                discount = BigDecimal.ZERO;
            }

            OrderItem item = new OrderItem();
            item.setProductId(itemRequest.getProductId());
            item.setProductName(itemRequest.getProductName());
            item.setProductSku(itemRequest.getProductSku());
            item.setQuantity(itemRequest.getQuantity());
            item.setOriginalUnitPrice(original);
            item.setUnitPrice(effective);
            item.setDiscountUnitAmount(discount);
            item.setSubtotal(effective.multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            
            // Usar addItem() que establece la relación bidireccional (item.setOrder(this))
            order.addItem(item);
        }

        // Guardar orden - cascade persiste los items automáticamente
        Order savedOrder = orderRepository.save(order);
        return convertToDTO(savedOrder);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        validateStatus(status);
        order.setStatus(status);

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
            default:
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
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));
        orderRepository.delete(order);
    }

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

    private void validateStatus(String status) {
        Set<String> allowed = Set.of("Pendiente", "Pagado", "Enviado", "Entregado", "Cancelado");
        if (!allowed.contains(status)) {
            throw new IllegalArgumentException("Estado no valido");
        }
    }

    private BigDecimal calculateShippingCost(String region, String address) {
        if (region != null && !region.isBlank()) {
            return shippingCostForRegion(region);
        }
        if (address != null && !address.isBlank()) {
            String normalized = address.toLowerCase();
            if (normalized.contains("metropolitana")) {
                return new BigDecimal("3990");
            }
            if (normalized.contains("valparaiso") || normalized.contains("o'higgins")) {
                return new BigDecimal("4990");
            }
        }
        return new BigDecimal("5990");
    }

    private BigDecimal shippingCostForRegion(String region) {
        switch (region.trim().toLowerCase()) {
            case "metropolitana":
                return new BigDecimal("3990");
            case "valparaiso":
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
        dto.setIva(calculateIva(order.getSubtotal()));
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

        List<OrderItem> items = orderItemRepository.findByOrder_Id(order.getId());
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
                item.getOriginalUnitPrice(),
                item.getUnitPrice(),
                item.getDiscountUnitAmount(),
                item.getSubtotal()
        );
    }

    private BigDecimal calculateIva(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(IVA_RATE).setScale(2, RoundingMode.HALF_UP);
    }
}

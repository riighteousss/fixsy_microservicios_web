package com.fixsy.ordenes.controller;

import com.fixsy.ordenes.dto.OrderDTO;
import com.fixsy.ordenes.dto.OrderRequestDTO;
import com.fixsy.ordenes.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Controller", description = "API para gestión de órdenes de compra de Fixsy Parts")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping
    @Operation(summary = "Obtener todas las órdenes")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener orden por ID")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener órdenes de un usuario por ID")
    public ResponseEntity<List<OrderDTO>> getOrdersByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Obtener órdenes de un usuario por email")
    public ResponseEntity<List<OrderDTO>> getOrdersByUserEmail(@PathVariable String email) {
        try {
            String decodedEmail = java.net.URLDecoder.decode(email, java.nio.charset.StandardCharsets.UTF_8);
            return ResponseEntity.ok(orderService.getOrdersByUserEmail(decodedEmail));
        } catch (Exception e) {
            return ResponseEntity.ok(orderService.getOrdersByUserEmail(email));
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Obtener órdenes por estado")
    public ResponseEntity<List<OrderDTO>> getOrdersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @GetMapping("/pending-shipment")
    @Operation(summary = "Obtener órdenes pagadas pendientes de envío")
    public ResponseEntity<List<OrderDTO>> getOrdersPendingShipment() {
        return ResponseEntity.ok(orderService.getOrdersPendingShipment());
    }

    @GetMapping("/tracking/{trackingNumber}")
    @Operation(summary = "Obtener orden por número de seguimiento")
    public ResponseEntity<OrderDTO> getOrderByTrackingNumber(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(orderService.getOrderByTrackingNumber(trackingNumber));
    }

    @PostMapping
    @Operation(summary = "Crear nueva orden")
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderRequestDTO orderRequest) {
        return new ResponseEntity<>(orderService.createOrder(orderRequest), HttpStatus.CREATED);
    }

    @PostMapping("/public")
    @Operation(summary = "Crear orden pública sin userId (invitado)")
    public ResponseEntity<OrderDTO> createPublicOrder(@Valid @RequestBody OrderRequestDTO orderRequest) {
        return new ResponseEntity<>(orderService.createPublicOrder(orderRequest), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Actualizar estado de la orden")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @PutMapping("/{id}/tracking")
    @Operation(summary = "Actualizar número de seguimiento")
    public ResponseEntity<OrderDTO> updateTrackingNumber(
            @PathVariable Long id,
            @RequestParam String trackingNumber) {
        return ResponseEntity.ok(orderService.updateTrackingNumber(id, trackingNumber));
    }

    @PutMapping("/{id}/payment")
    @Operation(summary = "Registrar pago de la orden")
    public ResponseEntity<OrderDTO> updatePaymentReference(
            @PathVariable Long id,
            @RequestParam String paymentReference) {
        return ResponseEntity.ok(orderService.updatePaymentReference(id, paymentReference));
    }

    @PutMapping("/{id}/admin-note")
    @Operation(summary = "Agregar nota administrativa")
    public ResponseEntity<OrderDTO> addAdminNote(
            @PathVariable Long id,
            @RequestParam String note) {
        return ResponseEntity.ok(orderService.addAdminNote(id, note));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar orden")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoints de estadísticas
    @GetMapping("/stats/count/{status}")
    @Operation(summary = "Contar órdenes por estado")
    public ResponseEntity<Map<String, Object>> countByStatus(@PathVariable String status) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("count", orderService.countOrdersByStatus(status));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/user/{userId}/count")
    @Operation(summary = "Contar órdenes de un usuario")
    public ResponseEntity<Map<String, Object>> countByUser(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("count", orderService.countOrdersByUser(userId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/total-sales")
    @Operation(summary = "Obtener total de ventas")
    public ResponseEntity<Map<String, Object>> getTotalSales() {
        Map<String, Object> response = new HashMap<>();
        response.put("totalSales", orderService.getTotalSales());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/sales-period")
    @Operation(summary = "Obtener ventas por período")
    public ResponseEntity<Map<String, Object>> getSalesForPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        Map<String, Object> response = new HashMap<>();
        response.put("start", start);
        response.put("end", end);
        response.put("totalSales", orderService.getTotalSalesForPeriod(start, end));
        return ResponseEntity.ok(response);
    }
}

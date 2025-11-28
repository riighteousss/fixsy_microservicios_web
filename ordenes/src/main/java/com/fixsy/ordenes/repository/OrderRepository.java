package com.fixsy.ordenes.repository;

import com.fixsy.ordenes.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Buscar por usuario
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Order> findByUserEmailOrderByCreatedAtDesc(String userEmail);
    
    // Buscar por estado
    List<Order> findByStatusOrderByCreatedAtDesc(String status);
    
    // Buscar por usuario y estado
    List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);
    
    // Buscar por rango de fechas
    List<Order> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
    
    // Contar órdenes por estado
    Long countByStatus(String status);
    
    // Contar órdenes de un usuario
    Long countByUserId(Long userId);
    
    // Estadísticas - Total vendido
    @Query("SELECT SUM(o.total) FROM Order o WHERE o.status = 'Pagado' OR o.status = 'Enviado' OR o.status = 'Entregado'")
    java.math.BigDecimal getTotalSales();
    
    // Estadísticas - Total vendido en un período
    @Query("SELECT SUM(o.total) FROM Order o WHERE (o.status = 'Pagado' OR o.status = 'Enviado' OR o.status = 'Entregado') AND o.createdAt BETWEEN :start AND :end")
    java.math.BigDecimal getTotalSalesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // Órdenes pendientes de envío
    @Query("SELECT o FROM Order o WHERE o.status = 'Pagado' ORDER BY o.paidAt ASC")
    List<Order> findOrdersPendingShipment();
    
    // Buscar por tracking number
    java.util.Optional<Order> findByTrackingNumber(String trackingNumber);
}


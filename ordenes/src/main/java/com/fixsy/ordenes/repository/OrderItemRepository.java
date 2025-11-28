package com.fixsy.ordenes.repository;

import com.fixsy.ordenes.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    // Obtener items de una orden
    List<OrderItem> findByOrderId(Long orderId);
    
    // Eliminar items de una orden
    void deleteByOrderId(Long orderId);
    
    // Contar ventas de un producto
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.productId = :productId")
    Long getTotalSoldByProduct(@Param("productId") Long productId);
    
    // Productos más vendidos
    @Query("SELECT oi.productId, oi.productName, SUM(oi.quantity) as totalQty " +
           "FROM OrderItem oi GROUP BY oi.productId, oi.productName ORDER BY totalQty DESC")
    List<Object[]> getTopSellingProducts();
}


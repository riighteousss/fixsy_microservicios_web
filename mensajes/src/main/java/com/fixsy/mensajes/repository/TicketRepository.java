package com.fixsy.mensajes.repository;

import com.fixsy.mensajes.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    // Por usuario
    List<Ticket> findByUserIdOrderByUpdatedAtDesc(Long userId);
    List<Ticket> findByUserEmailOrderByUpdatedAtDesc(String userEmail);
    
    // Por estado
    List<Ticket> findByEstadoOrderByUpdatedAtDesc(String estado);
    
    // Por prioridad
    List<Ticket> findByPrioridadOrderByUpdatedAtDesc(String prioridad);
    
    // Por categoría
    List<Ticket> findByCategoriaOrderByUpdatedAtDesc(String categoria);
    
    // Asignados a un soporte
    List<Ticket> findByAssignedToOrderByUpdatedAtDesc(Long assignedTo);
    
    // Sin asignar
    List<Ticket> findByAssignedToIsNullAndEstadoOrderByCreatedAtAsc(String estado);
    
    // Por orden
    List<Ticket> findByOrderIdOrderByCreatedAtDesc(Long orderId);
    
    // Conteos
    Long countByEstado(String estado);
    Long countByUserId(Long userId);
    Long countByAssignedTo(Long assignedTo);
    Long countByAssignedToAndEstado(Long assignedTo, String estado);
    
    // Tickets abiertos de un usuario
    @Query("SELECT t FROM Ticket t WHERE t.userId = :userId AND t.estado != 'Cerrado' ORDER BY t.updatedAt DESC")
    List<Ticket> findOpenTicketsByUserId(@Param("userId") Long userId);
    
    // Tickets pendientes (abiertos + en proceso)
    @Query("SELECT t FROM Ticket t WHERE t.estado IN ('Abierto', 'En Proceso') ORDER BY " +
           "CASE t.prioridad WHEN 'Urgente' THEN 1 WHEN 'Alta' THEN 2 WHEN 'Media' THEN 3 ELSE 4 END, " +
           "t.createdAt ASC")
    List<Ticket> findPendingTicketsOrderByPriority();
}


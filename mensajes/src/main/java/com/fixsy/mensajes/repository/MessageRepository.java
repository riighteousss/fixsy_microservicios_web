package com.fixsy.mensajes.repository;

import com.fixsy.mensajes.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // Mensajes de un ticket
    List<Message> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
    
    // Mensajes no leídos de un ticket
    List<Message> findByTicketIdAndIsReadFalse(Long ticketId);
    
    // Contar no leídos de un ticket
    Long countByTicketIdAndIsReadFalse(Long ticketId);
    
    // Contar no leídos para un usuario (mensajes de otros en sus tickets)
    @Query("SELECT COUNT(m) FROM Message m WHERE m.ticketId IN " +
           "(SELECT t.id FROM Ticket t WHERE t.userId = :userId) " +
           "AND m.senderId != :userId AND m.isRead = false")
    Long countUnreadForUser(@Param("userId") Long userId);
    
    // Contar no leídos para soporte (mensajes en tickets asignados)
    @Query("SELECT COUNT(m) FROM Message m WHERE m.ticketId IN " +
           "(SELECT t.id FROM Ticket t WHERE t.assignedTo = :supportId) " +
           "AND m.senderId != :supportId AND m.isRead = false")
    Long countUnreadForSupport(@Param("supportId") Long supportId);
    
    // Marcar mensajes como leídos
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.ticketId = :ticketId AND m.senderId != :readerId")
    void markAsReadByTicketAndReader(@Param("ticketId") Long ticketId, @Param("readerId") Long readerId);
    
    // Eliminar mensajes de un ticket
    void deleteByTicketId(Long ticketId);
    
    // Último mensaje de un ticket
    @Query("SELECT m FROM Message m WHERE m.ticketId = :ticketId ORDER BY m.createdAt DESC LIMIT 1")
    Message findLastMessageByTicketId(@Param("ticketId") Long ticketId);
}


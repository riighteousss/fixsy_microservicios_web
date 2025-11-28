package com.fixsy.mensajes.service;

import com.fixsy.mensajes.dto.*;
import com.fixsy.mensajes.model.Message;
import com.fixsy.mensajes.model.Ticket;
import com.fixsy.mensajes.repository.MessageRepository;
import com.fixsy.mensajes.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private MessageRepository messageRepository;

    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TicketDTO getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));
        return convertToDTOWithMessages(ticket);
    }

    public List<TicketDTO> getTicketsByUserId(Long userId) {
        return ticketRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TicketDTO> getTicketsByUserEmail(String email) {
        return ticketRepository.findByUserEmailOrderByUpdatedAtDesc(email).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TicketDTO> getTicketsByEstado(String estado) {
        return ticketRepository.findByEstadoOrderByUpdatedAtDesc(estado).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TicketDTO> getTicketsByCategoria(String categoria) {
        return ticketRepository.findByCategoriaOrderByUpdatedAtDesc(categoria).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TicketDTO> getTicketsAssignedTo(Long supportId) {
        return ticketRepository.findByAssignedToOrderByUpdatedAtDesc(supportId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TicketDTO> getUnassignedTickets() {
        return ticketRepository.findByAssignedToIsNullAndEstadoOrderByCreatedAtAsc("Abierto").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TicketDTO> getPendingTickets() {
        return ticketRepository.findPendingTicketsOrderByPriority().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TicketDTO> getTicketsByOrderId(Long orderId) {
        return ticketRepository.findByOrderIdOrderByCreatedAtDesc(orderId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TicketDTO createTicket(TicketRequestDTO request) {
        Ticket ticket = new Ticket();
        ticket.setUserId(request.getUserId());
        ticket.setUserEmail(request.getUserEmail());
        ticket.setUserName(request.getUserName());
        ticket.setAsunto(request.getAsunto());
        ticket.setCategoria(request.getCategoria());
        ticket.setPrioridad(request.getPrioridad() != null ? request.getPrioridad() : "Media");
        ticket.setEstado("Abierto");
        ticket.setOrderId(request.getOrderId());

        Ticket savedTicket = ticketRepository.save(ticket);

        // Crear mensaje inicial
        Message initialMessage = new Message();
        initialMessage.setTicketId(savedTicket.getId());
        initialMessage.setSenderId(request.getUserId());
        initialMessage.setSenderEmail(request.getUserEmail());
        initialMessage.setSenderName(request.getUserName());
        initialMessage.setSenderRole("Usuario");
        initialMessage.setContenido(request.getMensajeInicial());
        initialMessage.setIsRead(false);
        messageRepository.save(initialMessage);

        return convertToDTOWithMessages(savedTicket);
    }

    @Transactional
    public TicketDTO updateTicketEstado(Long id, String estado) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

        ticket.setEstado(estado);
        if ("Cerrado".equals(estado) || "Resuelto".equals(estado)) {
            ticket.setClosedAt(LocalDateTime.now());
        }

        Ticket updated = ticketRepository.save(ticket);
        return convertToDTO(updated);
    }

    @Transactional
    public TicketDTO updateTicketPrioridad(Long id, String prioridad) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

        ticket.setPrioridad(prioridad);
        Ticket updated = ticketRepository.save(ticket);
        return convertToDTO(updated);
    }

    @Transactional
    public TicketDTO assignTicket(Long id, Long supportId, String supportName) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

        ticket.setAssignedTo(supportId);
        ticket.setAssignedName(supportName);
        if ("Abierto".equals(ticket.getEstado())) {
            ticket.setEstado("En Proceso");
        }

        Ticket updated = ticketRepository.save(ticket);
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new RuntimeException("Ticket no encontrado");
        }
        messageRepository.deleteByTicketId(id);
        ticketRepository.deleteById(id);
    }

    // Estadísticas
    public Long countByEstado(String estado) {
        return ticketRepository.countByEstado(estado);
    }

    public Long countByUserId(Long userId) {
        return ticketRepository.countByUserId(userId);
    }

    public Long countAssignedTo(Long supportId) {
        return ticketRepository.countByAssignedTo(supportId);
    }

    public Long countUnreadForUser(Long userId) {
        return messageRepository.countUnreadForUser(userId);
    }

    public Long countUnreadForSupport(Long supportId) {
        return messageRepository.countUnreadForSupport(supportId);
    }

    private TicketDTO convertToDTO(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setUserId(ticket.getUserId());
        dto.setUserEmail(ticket.getUserEmail());
        dto.setUserName(ticket.getUserName());
        dto.setAsunto(ticket.getAsunto());
        dto.setCategoria(ticket.getCategoria());
        dto.setEstado(ticket.getEstado());
        dto.setPrioridad(ticket.getPrioridad());
        dto.setAssignedTo(ticket.getAssignedTo());
        dto.setAssignedName(ticket.getAssignedName());
        dto.setOrderId(ticket.getOrderId());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());
        dto.setClosedAt(ticket.getClosedAt());
        
        // Contar no leídos
        Long unread = messageRepository.countByTicketIdAndIsReadFalse(ticket.getId());
        dto.setUnreadCount(unread != null ? unread.intValue() : 0);
        
        return dto;
    }

    private TicketDTO convertToDTOWithMessages(Ticket ticket) {
        TicketDTO dto = convertToDTO(ticket);
        
        // Cargar mensajes
        List<Message> messages = messageRepository.findByTicketIdOrderByCreatedAtAsc(ticket.getId());
        dto.setMensajes(messages.stream().map(this::convertMessageToDTO).collect(Collectors.toList()));
        
        return dto;
    }

    private MessageDTO convertMessageToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setTicketId(message.getTicketId());
        dto.setSenderId(message.getSenderId());
        dto.setSenderEmail(message.getSenderEmail());
        dto.setSenderName(message.getSenderName());
        dto.setSenderRole(message.getSenderRole());
        dto.setContenido(message.getContenido());
        dto.setIsRead(message.getIsRead());
        dto.setCreatedAt(message.getCreatedAt());
        
        if (message.getAdjuntos() != null && !message.getAdjuntos().isBlank()) {
            dto.setAdjuntos(Arrays.asList(message.getAdjuntos().split(",")));
        } else {
            dto.setAdjuntos(Collections.emptyList());
        }
        
        return dto;
    }
}


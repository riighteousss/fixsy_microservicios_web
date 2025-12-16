package com.fixsy.mensajes.service;

import com.fixsy.mensajes.dto.MessageDTO;
import com.fixsy.mensajes.dto.MessageRequestDTO;
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
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TicketRepository ticketRepository;

    public List<MessageDTO> getMessagesByTicketId(Long ticketId) {
        return getMessagesByTicketId(ticketId, "Usuario");
    }

    public List<MessageDTO> getMessagesByTicketId(Long ticketId, String viewerRole) {
        List<Message> messages;
        if ("Usuario".equalsIgnoreCase(viewerRole)) {
            messages = messageRepository.findByTicket_IdAndInternalFalseOrderByCreatedAtAsc(ticketId);
        } else {
            messages = messageRepository.findByTicket_IdOrderByCreatedAtAsc(ticketId);
        }
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageDTO sendMessage(MessageRequestDTO request) {
        Ticket ticket = ticketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

        if ("Cerrado".equals(ticket.getEstado())) {
            throw new RuntimeException("No se puede enviar mensajes a un ticket cerrado");
        }

        Message message = new Message();
        message.setTicket(ticket);
        message.setSenderId(request.getSenderId());
        message.setSenderEmail(request.getSenderEmail());
        message.setSenderName(request.getSenderName());
        message.setSenderRole(request.getSenderRole());
        message.setContenido(request.getContenido());
        message.setIsRead(false);

        if (request.getAdjuntos() != null && !request.getAdjuntos().isEmpty()) {
            message.setAdjuntos(String.join(",", request.getAdjuntos()));
        }

        boolean wantsInternal = request.getInternal() != null && request.getInternal();
        if (wantsInternal) {
            String role = request.getSenderRole();
            if (role == null || (!role.equalsIgnoreCase("Soporte") && !role.equalsIgnoreCase("Admin"))) {
                throw new RuntimeException("Solo Soporte o Admin pueden enviar mensajes internos");
            }
        }
        message.setInternal(wantsInternal);

        ticket.addMessage(message);
        Message saved = messageRepository.save(message);

        ticket.setUpdatedAt(LocalDateTime.now());
        if (("Soporte".equals(request.getSenderRole()) || "Admin".equals(request.getSenderRole()))
                && "Abierto".equals(ticket.getEstado())) {
            ticket.setEstado("En Proceso");
        }
        ticketRepository.save(ticket);

        return convertToDTO(saved);
    }

    @Transactional
    public void markMessagesAsRead(Long ticketId, Long readerId) {
        messageRepository.markAsReadByTicketAndReader(ticketId, readerId);
    }

    public Long countUnreadForUser(Long userId) {
        return messageRepository.countUnreadForUser(userId);
    }

    public Long countUnreadForSupport(Long supportId) {
        return messageRepository.countUnreadForSupport(supportId);
    }

    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setTicketId(message.getTicket() != null ? message.getTicket().getId() : null);
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

        dto.setInternal(message.getInternal());
        return dto;
    }
}

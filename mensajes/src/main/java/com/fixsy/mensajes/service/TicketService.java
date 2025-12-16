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
        return createTicketInternal(
                request.getUserId(),
                request.getUserEmail(),
                request.getUserName(),
                request.getAsunto(),
                request.getCategoria(),
                request.getPrioridad(),
                request.getOrderId(),
                request.getMensajeInicial(),
                "Usuario"
        );
    }

    @Transactional
    public TicketDTO createPublicTicket(PublicTicketRequestDTO request) {
        if (request.getOrderId() == null || request.getOrderId() <= 0) {
            throw new IllegalArgumentException("El orderId es obligatorio para solicitar boleta");
        }
        String asunto = "Boleta de compra #" + request.getOrderId();
        String mensaje = "Solicito boleta de la orden #" + request.getOrderId();
        if (request.getNote() != null && !request.getNote().isBlank()) {
            mensaje += " - " + request.getNote().trim();
        }

        return createTicketInternal(
                null,
                request.getUserEmail(),
                request.getUserName(),
                asunto,
                "Boleta",
                request.getPrioridad(),
                request.getOrderId(),
                mensaje,
                "Usuario"
        );
    }

    @Transactional
    public TicketDTO createPasswordRecoveryTicket(PasswordRecoveryRequestDTO request) {
        String generatedMessage = "El usuario con email " + request.getUserEmail() + " solicito recuperar su contrasena.";
        return createTicketInternal(
                request.getUserId(),
                request.getUserEmail(),
                request.getUserName(),
                "Solicitud de recuperacion de contrasena",
                "Recuperacion de contrasena",
                "Alta",
                null,
                generatedMessage,
                "Usuario"
        );
    }

    @Transactional
    public TicketDTO updateTicketEstado(Long id, String estado) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));

        if (!isValidEstadoTransition(ticket.getEstado(), estado)) {
            throw new RuntimeException("Transicion de estado no permitida");
        }

        ticket.setEstado(estado);
        if ("Cerrado".equals(estado)) {
            ticket.setClosedAt(LocalDateTime.now());
        }

        Ticket updated = ticketRepository.save(ticket);
        return convertToDTO(updated);
    }

    @Transactional
    public TicketDTO closeTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));
        ticket.setEstado("Cerrado");
        ticket.setClosedAt(LocalDateTime.now());
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
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));
        ticketRepository.delete(ticket);
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

    public TicketDTO getTicketByIdForUser(Long id, Long userId, String userEmail) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket no encontrado"));
        boolean matchesUser = (userId != null && userId.equals(ticket.getUserId()));
        boolean matchesEmail = (userEmail != null && userEmail.equalsIgnoreCase(ticket.getUserEmail()));
        if (!matchesUser && !matchesEmail) {
            throw new RuntimeException("Ticket no encontrado o sin permisos");
        }
        return convertToDTOWithMessages(ticket);
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
        Long unread = messageRepository.countByTicket_IdAndIsReadFalse(ticket.getId());
        dto.setUnreadCount(unread != null ? unread.intValue() : 0);
        
        return dto;
    }

    private TicketDTO convertToDTOWithMessages(Ticket ticket) {
        TicketDTO dto = convertToDTO(ticket);
        
        // Cargar mensajes
        List<Message> messages = messageRepository.findByTicket_IdOrderByCreatedAtAsc(ticket.getId());
        dto.setMensajes(messages.stream().map(this::convertMessageToDTO).collect(Collectors.toList()));
        
        return dto;
    }

    private MessageDTO convertMessageToDTO(Message message) {
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

    private TicketDTO createTicketInternal(Long userId,
                                           String userEmail,
                                           String userName,
                                           String asunto,
                                           String categoria,
                                           String prioridad,
                                           Long orderId,
                                           String mensajeInicial,
                                           String senderRole) {
        if (mensajeInicial == null || mensajeInicial.isBlank()) {
            throw new IllegalArgumentException("El mensaje inicial es obligatorio");
        }
        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException("El email del usuario es obligatorio");
        }

        String categoriaFinal = categoria != null && !categoria.isBlank() ? categoria : "Consulta";
        String asuntoFinal = asunto;
        if ("Boleta".equalsIgnoreCase(categoriaFinal)) {
            if (orderId == null) {
                throw new IllegalArgumentException("Los tickets de boleta requieren un orderId");
            }
            asuntoFinal = "Boleta de compra #" + orderId;
        }
        if (asuntoFinal == null || asuntoFinal.isBlank()) {
            throw new IllegalArgumentException("El asunto es obligatorio");
        }

        Ticket ticket = new Ticket();
        ticket.setUserId(userId);
        ticket.setUserEmail(userEmail);
        ticket.setUserName(userName);
        ticket.setAsunto(asuntoFinal);
        ticket.setCategoria(categoriaFinal);
        ticket.setPrioridad(prioridad != null && !prioridad.isBlank() ? prioridad : "Media");
        ticket.setEstado("Abierto");
        ticket.setOrderId(orderId);

        Message initialMessage = new Message();
        initialMessage.setSenderId(userId);
        initialMessage.setSenderEmail(userEmail);
        initialMessage.setSenderName(userName);
        initialMessage.setSenderRole(senderRole);
        initialMessage.setContenido(mensajeInicial);
        initialMessage.setIsRead(false);
        ticket.addMessage(initialMessage);

        Ticket savedTicket = ticketRepository.save(ticket);
        savedTicket.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(savedTicket);

        return convertToDTOWithMessages(savedTicket);
    }

    private boolean isValidEstadoTransition(String current, String target) {
        if (target == null || current == null) return false;
        if (current.equals(target)) return true;
        return switch (current) {
            case "Abierto" -> target.equals("En Proceso") || target.equals("Resuelto") || target.equals("Cerrado");
            case "En Proceso" -> target.equals("Resuelto") || target.equals("Cerrado");
            case "Resuelto" -> target.equals("Cerrado") || target.equals("En Proceso");
            default -> false;
        };
    }
}

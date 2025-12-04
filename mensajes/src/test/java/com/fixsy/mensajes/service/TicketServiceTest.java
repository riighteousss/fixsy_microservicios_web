package com.fixsy.mensajes.service;

import com.fixsy.mensajes.dto.MessageDTO;
import com.fixsy.mensajes.dto.TicketDTO;
import com.fixsy.mensajes.dto.TicketRequestDTO;
import com.fixsy.mensajes.model.Message;
import com.fixsy.mensajes.model.Ticket;
import com.fixsy.mensajes.repository.MessageRepository;
import com.fixsy.mensajes.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private TicketService ticketService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTicketWithInitialMessage_success() {
        TicketRequestDTO request = new TicketRequestDTO(
                1L,
                "user@example.com",
                "User",
                "Problema",
                "Consulta",
                "Alta",
                10L,
                "Mensaje inicial"
        );

        given(ticketRepository.save(any(Ticket.class))).willAnswer(invocation -> {
            Ticket t = invocation.getArgument(0);
            t.setId(5L);
            t.setCreatedAt(LocalDateTime.now());
            t.setUpdatedAt(LocalDateTime.now());
            return t;
        });
        given(messageRepository.save(any(Message.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(messageRepository.countByTicket_IdAndIsReadFalse(5L)).willReturn(1L);

        Message initial = new Message();
        Ticket t = new Ticket();
        t.setId(5L);
        initial.setTicket(t);
        initial.setSenderRole("Usuario");
        initial.setContenido("Mensaje inicial");
        given(messageRepository.findByTicket_IdOrderByCreatedAtAsc(5L)).willReturn(List.of(initial));

        TicketDTO result = ticketService.createTicket(request);

        assertEquals("Abierto", result.getEstado());
        assertEquals("Alta", result.getPrioridad());
        assertEquals(1, result.getMensajes().size());
        MessageDTO msg = result.getMensajes().get(0);
        assertEquals("Usuario", msg.getSenderRole());
        assertEquals("Mensaje inicial", msg.getContenido());
        assertEquals(1, result.getUnreadCount());
    }

    @Test
    void updateTicketEstado_setsClosedAtWhenClosed() {
        Ticket ticket = new Ticket();
        ticket.setId(7L);
        ticket.setEstado("Abierto");
        given(ticketRepository.findById(7L)).willReturn(Optional.of(ticket));
        given(ticketRepository.save(any(Ticket.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(messageRepository.countByTicket_IdAndIsReadFalse(7L)).willReturn(0L);
        given(messageRepository.findByTicket_IdOrderByCreatedAtAsc(7L)).willReturn(Collections.emptyList());

        TicketDTO result = ticketService.updateTicketEstado(7L, "Cerrado");

        assertEquals("Cerrado", result.getEstado());
        assertNotNull(result.getClosedAt());
    }

    @Test
    void createTicket_invalidData_throwsException() {
        TicketRequestDTO request = new TicketRequestDTO();
        request.setUserId(1L);
        request.setUserEmail("user@example.com");
        request.setAsunto("");
        request.setMensajeInicial("");

        assertThrows(IllegalArgumentException.class, () -> ticketService.createTicket(request));
    }
}

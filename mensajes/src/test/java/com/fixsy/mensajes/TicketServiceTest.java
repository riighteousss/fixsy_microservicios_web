package com.fixsy.mensajes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fixsy.mensajes.dto.TicketDTO;
import com.fixsy.mensajes.dto.TicketRequestDTO;
import com.fixsy.mensajes.model.Message;
import com.fixsy.mensajes.model.Ticket;
import com.fixsy.mensajes.repository.MessageRepository;
import com.fixsy.mensajes.repository.TicketRepository;
import com.fixsy.mensajes.service.TicketService;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private TicketService ticketService;

    private Ticket testTicket;
    private Message testMessage;
    private TicketRequestDTO testTicketRequest;

    @BeforeEach
    void setUp() {
        testTicket = new Ticket();
        testTicket.setId(1L);
        testTicket.setUserId(1L);
        testTicket.setUserEmail("cliente@example.com");
        testTicket.setUserName("Juan Pérez");
        testTicket.setAsunto("Problema con mi pedido");
        testTicket.setCategoria("Reclamo");
        testTicket.setEstado("Abierto");
        testTicket.setPrioridad("Media");
        testTicket.setCreatedAt(LocalDateTime.now());
        testTicket.setUpdatedAt(LocalDateTime.now());

        testMessage = new Message();
        testMessage.setId(1L);
        testMessage.setTicketId(1L);
        testMessage.setSenderId(1L);
        testMessage.setSenderEmail("cliente@example.com");
        testMessage.setSenderName("Juan Pérez");
        testMessage.setSenderRole("Usuario");
        testMessage.setContenido("Mensaje de prueba");
        testMessage.setIsRead(false);
        testMessage.setCreatedAt(LocalDateTime.now());

        testTicketRequest = new TicketRequestDTO();
        testTicketRequest.setUserId(1L);
        testTicketRequest.setUserEmail("cliente@example.com");
        testTicketRequest.setUserName("Juan Pérez");
        testTicketRequest.setAsunto("Problema con mi pedido");
        testTicketRequest.setCategoria("Reclamo");
        testTicketRequest.setPrioridad("Media");
        testTicketRequest.setMensajeInicial("Descripción del problema");
    }

    @Test
    void testGetAllTickets() {
        when(ticketRepository.findAll()).thenReturn(Arrays.asList(testTicket));
        when(messageRepository.countByTicketIdAndIsReadFalse(1L)).thenReturn(0L);

        List<TicketDTO> result = ticketService.getAllTickets();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ticketRepository).findAll();
    }

    @Test
    void testGetTicketById() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(messageRepository.countByTicketIdAndIsReadFalse(1L)).thenReturn(0L);
        when(messageRepository.findByTicketIdOrderByCreatedAtAsc(1L)).thenReturn(Arrays.asList(testMessage));

        TicketDTO result = ticketService.getTicketById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNotNull(result.getMensajes());
    }

    @Test
    void testGetTicketByIdNotFound() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ticketService.getTicketById(99L));
    }

    @Test
    void testGetTicketsByUserId() {
        when(ticketRepository.findByUserIdOrderByUpdatedAtDesc(1L)).thenReturn(Arrays.asList(testTicket));
        when(messageRepository.countByTicketIdAndIsReadFalse(1L)).thenReturn(0L);

        List<TicketDTO> result = ticketService.getTicketsByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetTicketsByUserEmail() {
        when(ticketRepository.findByUserEmailOrderByUpdatedAtDesc("cliente@example.com"))
            .thenReturn(Arrays.asList(testTicket));
        when(messageRepository.countByTicketIdAndIsReadFalse(1L)).thenReturn(0L);

        List<TicketDTO> result = ticketService.getTicketsByUserEmail("cliente@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetTicketsByEstado() {
        when(ticketRepository.findByEstadoOrderByUpdatedAtDesc("Abierto"))
            .thenReturn(Arrays.asList(testTicket));
        when(messageRepository.countByTicketIdAndIsReadFalse(1L)).thenReturn(0L);

        List<TicketDTO> result = ticketService.getTicketsByEstado("Abierto");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetTicketsByCategoria() {
        when(ticketRepository.findByCategoriaOrderByUpdatedAtDesc("Reclamo"))
            .thenReturn(Arrays.asList(testTicket));
        when(messageRepository.countByTicketIdAndIsReadFalse(1L)).thenReturn(0L);

        List<TicketDTO> result = ticketService.getTicketsByCategoria("Reclamo");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetTicketsAssignedTo() {
        testTicket.setAssignedTo(2L);
        when(ticketRepository.findByAssignedToOrderByUpdatedAtDesc(2L))
            .thenReturn(Arrays.asList(testTicket));
        when(messageRepository.countByTicketIdAndIsReadFalse(1L)).thenReturn(0L);

        List<TicketDTO> result = ticketService.getTicketsAssignedTo(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetUnassignedTickets() {
        when(ticketRepository.findByAssignedToIsNullAndEstadoOrderByCreatedAtAsc("Abierto"))
            .thenReturn(Arrays.asList(testTicket));
        when(messageRepository.countByTicketIdAndIsReadFalse(1L)).thenReturn(0L);

        List<TicketDTO> result = ticketService.getUnassignedTickets();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetPendingTickets() {
        when(ticketRepository.findPendingTicketsOrderByPriority())
            .thenReturn(Arrays.asList(testTicket));
        when(messageRepository.countByTicketIdAndIsReadFalse(1L)).thenReturn(0L);

        List<TicketDTO> result = ticketService.getPendingTickets();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetTicketsByOrderId() {
        testTicket.setOrderId(100L);
        when(ticketRepository.findByOrderIdOrderByCreatedAtDesc(100L))
            .thenReturn(Arrays.asList(testTicket));
        when(messageRepository.countByTicketIdAndIsReadFalse(1L)).thenReturn(0L);

        List<TicketDTO> result = ticketService.getTicketsByOrderId(100L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testCreateTicket() {
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);
        when(messageRepository.countByTicketIdAndIsReadFalse(1L)).thenReturn(1L);
        when(messageRepository.findByTicketIdOrderByCreatedAtAsc(1L)).thenReturn(Arrays.asList(testMessage));

        TicketDTO result = ticketService.createTicket(testTicketRequest);

        assertNotNull(result);
        assertEquals("cliente@example.com", result.getUserEmail());
        verify(ticketRepository).save(any(Ticket.class));
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void testUpdateTicketEstado() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        when(messageRepository.countByTicketIdAndIsReadFalse(1L)).thenReturn(0L);

        TicketDTO result = ticketService.updateTicketEstado(1L, "En Proceso");

        assertNotNull(result);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void testUpdateTicketEstadoCerrado() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        when(messageRepository.countByTicketIdAndIsReadFalse(1L)).thenReturn(0L);

        TicketDTO result = ticketService.updateTicketEstado(1L, "Cerrado");

        assertNotNull(result);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void testUpdateTicketPrioridad() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        when(messageRepository.countByTicketIdAndIsReadFalse(1L)).thenReturn(0L);

        TicketDTO result = ticketService.updateTicketPrioridad(1L, "Alta");

        assertNotNull(result);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void testAssignTicket() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        when(messageRepository.countByTicketIdAndIsReadFalse(1L)).thenReturn(0L);

        TicketDTO result = ticketService.assignTicket(1L, 2L, "María García");

        assertNotNull(result);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void testDeleteTicket() {
        when(ticketRepository.existsById(1L)).thenReturn(true);
        doNothing().when(messageRepository).deleteByTicketId(1L);
        doNothing().when(ticketRepository).deleteById(1L);

        ticketService.deleteTicket(1L);

        verify(messageRepository).deleteByTicketId(1L);
        verify(ticketRepository).deleteById(1L);
    }

    @Test
    void testDeleteTicketNotFound() {
        when(ticketRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> ticketService.deleteTicket(99L));
    }

    @Test
    void testCountByEstado() {
        when(ticketRepository.countByEstado("Abierto")).thenReturn(5L);

        Long result = ticketService.countByEstado("Abierto");

        assertEquals(5L, result);
    }

    @Test
    void testCountByUserId() {
        when(ticketRepository.countByUserId(1L)).thenReturn(3L);

        Long result = ticketService.countByUserId(1L);

        assertEquals(3L, result);
    }

    @Test
    void testCountAssignedTo() {
        when(ticketRepository.countByAssignedTo(2L)).thenReturn(7L);

        Long result = ticketService.countAssignedTo(2L);

        assertEquals(7L, result);
    }

    @Test
    void testCountUnreadForUser() {
        when(messageRepository.countUnreadForUser(1L)).thenReturn(4L);

        Long result = ticketService.countUnreadForUser(1L);

        assertEquals(4L, result);
    }

    @Test
    void testCountUnreadForSupport() {
        when(messageRepository.countUnreadForSupport(2L)).thenReturn(10L);

        Long result = ticketService.countUnreadForSupport(2L);

        assertEquals(10L, result);
    }
}


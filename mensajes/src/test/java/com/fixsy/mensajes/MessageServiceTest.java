package com.fixsy.mensajes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fixsy.mensajes.dto.MessageDTO;
import com.fixsy.mensajes.dto.MessageRequestDTO;
import com.fixsy.mensajes.model.Message;
import com.fixsy.mensajes.model.Ticket;
import com.fixsy.mensajes.repository.MessageRepository;
import com.fixsy.mensajes.repository.TicketRepository;
import com.fixsy.mensajes.service.MessageService;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private MessageService messageService;

    private Ticket testTicket;
    private Message testMessage;
    private MessageRequestDTO testMessageRequest;

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

        testMessageRequest = new MessageRequestDTO();
        testMessageRequest.setTicketId(1L);
        testMessageRequest.setSenderId(1L);
        testMessageRequest.setSenderEmail("cliente@example.com");
        testMessageRequest.setSenderName("Juan Pérez");
        testMessageRequest.setSenderRole("Usuario");
        testMessageRequest.setContenido("Mensaje de respuesta");
    }

    @Test
    void testGetMessagesByTicketId() {
        when(messageRepository.findByTicketIdOrderByCreatedAtAsc(1L))
            .thenReturn(Arrays.asList(testMessage));

        List<MessageDTO> result = messageService.getMessagesByTicketId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Mensaje de prueba", result.get(0).getContenido());
    }

    @Test
    void testSendMessage() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);

        MessageDTO result = messageService.sendMessage(testMessageRequest);

        assertNotNull(result);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void testSendMessageTicketNotFound() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());
        testMessageRequest.setTicketId(99L);

        assertThrows(RuntimeException.class, () -> messageService.sendMessage(testMessageRequest));
    }

    @Test
    void testSendMessageTicketClosed() {
        testTicket.setEstado("Cerrado");
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));

        assertThrows(RuntimeException.class, () -> messageService.sendMessage(testMessageRequest));
    }

    @Test
    void testSendMessageFromSupportChangesStatus() {
        testMessageRequest.setSenderRole("Soporte");
        testMessageRequest.setSenderId(2L);
        
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);

        MessageDTO result = messageService.sendMessage(testMessageRequest);

        assertNotNull(result);
        verify(ticketRepository, times(2)).save(any(Ticket.class));
    }

    @Test
    void testSendMessageFromAdminChangesStatus() {
        testMessageRequest.setSenderRole("Admin");
        testMessageRequest.setSenderId(3L);
        
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);

        MessageDTO result = messageService.sendMessage(testMessageRequest);

        assertNotNull(result);
        verify(ticketRepository, times(2)).save(any(Ticket.class));
    }

    @Test
    void testSendMessageWithAttachments() {
        testMessageRequest.setAdjuntos(Arrays.asList("url1.jpg", "url2.pdf"));
        
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));
        when(messageRepository.save(any(Message.class))).thenReturn(testMessage);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);

        MessageDTO result = messageService.sendMessage(testMessageRequest);

        assertNotNull(result);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void testMarkMessagesAsRead() {
        doNothing().when(messageRepository).markAsReadByTicketAndReader(1L, 2L);

        messageService.markMessagesAsRead(1L, 2L);

        verify(messageRepository).markAsReadByTicketAndReader(1L, 2L);
    }

    @Test
    void testCountUnreadForUser() {
        when(messageRepository.countUnreadForUser(1L)).thenReturn(5L);

        Long result = messageService.countUnreadForUser(1L);

        assertEquals(5L, result);
    }

    @Test
    void testCountUnreadForSupport() {
        when(messageRepository.countUnreadForSupport(2L)).thenReturn(8L);

        Long result = messageService.countUnreadForSupport(2L);

        assertEquals(8L, result);
    }
}


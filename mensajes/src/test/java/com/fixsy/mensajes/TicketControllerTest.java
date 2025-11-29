package com.fixsy.mensajes;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fixsy.mensajes.controller.TicketController;
import com.fixsy.mensajes.dto.MessageDTO;
import com.fixsy.mensajes.dto.TicketDTO;
import com.fixsy.mensajes.dto.TicketRequestDTO;
import com.fixsy.mensajes.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebMvcTest(TicketController.class)
public class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketService ticketService;

    private ObjectMapper objectMapper;
    private TicketDTO testTicketDTO;
    private TicketRequestDTO testTicketRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(1L);
        messageDTO.setTicketId(1L);
        messageDTO.setSenderId(1L);
        messageDTO.setSenderEmail("cliente@example.com");
        messageDTO.setSenderName("Juan Pérez");
        messageDTO.setSenderRole("Usuario");
        messageDTO.setContenido("Mensaje de prueba");
        messageDTO.setIsRead(false);
        messageDTO.setAdjuntos(Collections.emptyList());
        messageDTO.setCreatedAt(LocalDateTime.now());

        testTicketDTO = new TicketDTO();
        testTicketDTO.setId(1L);
        testTicketDTO.setUserId(1L);
        testTicketDTO.setUserEmail("cliente@example.com");
        testTicketDTO.setUserName("Juan Pérez");
        testTicketDTO.setAsunto("Problema con mi pedido");
        testTicketDTO.setCategoria("Reclamo");
        testTicketDTO.setEstado("Abierto");
        testTicketDTO.setPrioridad("Media");
        testTicketDTO.setUnreadCount(1);
        testTicketDTO.setMensajes(Arrays.asList(messageDTO));
        testTicketDTO.setCreatedAt(LocalDateTime.now());
        testTicketDTO.setUpdatedAt(LocalDateTime.now());

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
    void testGetAllTickets() throws Exception {
        when(ticketService.getAllTickets()).thenReturn(Arrays.asList(testTicketDTO));

        mockMvc.perform(get("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].asunto").value("Problema con mi pedido"));
    }

    @Test
    void testGetTicketById() throws Exception {
        when(ticketService.getTicketById(1L)).thenReturn(testTicketDTO);

        mockMvc.perform(get("/api/tickets/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.asunto").value("Problema con mi pedido"));
    }

    @Test
    void testGetTicketsByUserId() throws Exception {
        when(ticketService.getTicketsByUserId(1L)).thenReturn(Arrays.asList(testTicketDTO));

        mockMvc.perform(get("/api/tickets/user/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetTicketsByUserEmail() throws Exception {
        when(ticketService.getTicketsByUserEmail("cliente@example.com"))
            .thenReturn(Arrays.asList(testTicketDTO));

        mockMvc.perform(get("/api/tickets/email/cliente@example.com")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetTicketsByEstado() throws Exception {
        when(ticketService.getTicketsByEstado("Abierto")).thenReturn(Arrays.asList(testTicketDTO));

        mockMvc.perform(get("/api/tickets/estado/Abierto")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].estado").value("Abierto"));
    }

    @Test
    void testGetTicketsByCategoria() throws Exception {
        when(ticketService.getTicketsByCategoria("Reclamo")).thenReturn(Arrays.asList(testTicketDTO));

        mockMvc.perform(get("/api/tickets/categoria/Reclamo")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].categoria").value("Reclamo"));
    }

    @Test
    void testGetTicketsAssignedTo() throws Exception {
        testTicketDTO.setAssignedTo(2L);
        when(ticketService.getTicketsAssignedTo(2L)).thenReturn(Arrays.asList(testTicketDTO));

        mockMvc.perform(get("/api/tickets/assigned/2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetUnassignedTickets() throws Exception {
        when(ticketService.getUnassignedTickets()).thenReturn(Arrays.asList(testTicketDTO));

        mockMvc.perform(get("/api/tickets/unassigned")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetPendingTickets() throws Exception {
        when(ticketService.getPendingTickets()).thenReturn(Arrays.asList(testTicketDTO));

        mockMvc.perform(get("/api/tickets/pending")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetTicketsByOrderId() throws Exception {
        testTicketDTO.setOrderId(100L);
        when(ticketService.getTicketsByOrderId(100L)).thenReturn(Arrays.asList(testTicketDTO));

        mockMvc.perform(get("/api/tickets/order/100")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testCreateTicket() throws Exception {
        when(ticketService.createTicket(Mockito.any(TicketRequestDTO.class))).thenReturn(testTicketDTO);

        mockMvc.perform(post("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTicketRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.asunto").value("Problema con mi pedido"));
    }

    @Test
    void testUpdateTicketEstado() throws Exception {
        when(ticketService.updateTicketEstado(1L, "En Proceso")).thenReturn(testTicketDTO);

        mockMvc.perform(put("/api/tickets/1/estado")
                .param("estado", "En Proceso")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void testUpdateTicketPrioridad() throws Exception {
        when(ticketService.updateTicketPrioridad(1L, "Alta")).thenReturn(testTicketDTO);

        mockMvc.perform(put("/api/tickets/1/prioridad")
                .param("prioridad", "Alta")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void testAssignTicket() throws Exception {
        when(ticketService.assignTicket(1L, 2L, "María García")).thenReturn(testTicketDTO);

        mockMvc.perform(put("/api/tickets/1/assign")
                .param("supportId", "2")
                .param("supportName", "María García")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void testDeleteTicket() throws Exception {
        doNothing().when(ticketService).deleteTicket(1L);

        mockMvc.perform(delete("/api/tickets/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        verify(ticketService).deleteTicket(1L);
    }

    @Test
    void testCountByEstado() throws Exception {
        when(ticketService.countByEstado("Abierto")).thenReturn(5L);

        mockMvc.perform(get("/api/tickets/stats/count/Abierto")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("Abierto"))
            .andExpect(jsonPath("$.count").value(5));
    }

    @Test
    void testCountByUserId() throws Exception {
        when(ticketService.countByUserId(1L)).thenReturn(3L);
        when(ticketService.countUnreadForUser(1L)).thenReturn(2L);

        mockMvc.perform(get("/api/tickets/stats/user/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.count").value(3))
            .andExpect(jsonPath("$.unread").value(2));
    }

    @Test
    void testStatsBySupport() throws Exception {
        when(ticketService.countAssignedTo(2L)).thenReturn(7L);
        when(ticketService.countUnreadForSupport(2L)).thenReturn(4L);

        mockMvc.perform(get("/api/tickets/stats/support/2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.supportId").value(2))
            .andExpect(jsonPath("$.assigned").value(7))
            .andExpect(jsonPath("$.unread").value(4));
    }
}


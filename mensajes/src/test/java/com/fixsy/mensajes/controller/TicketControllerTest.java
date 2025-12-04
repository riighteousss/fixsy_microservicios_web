package com.fixsy.mensajes.controller;

import com.fixsy.mensajes.dto.MessageDTO;
import com.fixsy.mensajes.dto.TicketDTO;
import com.fixsy.mensajes.dto.TicketRequestDTO;
import com.fixsy.mensajes.service.MessageService;
import com.fixsy.mensajes.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
@AutoConfigureMockMvc(addFilters = false) // desactiva filtros de seguridad para las pruebas
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TicketService ticketService;

    @MockitoBean
    private MessageService messageService; 

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTicket_shouldReturn201WithBody() throws Exception {
        TicketDTO dto = new TicketDTO();
        dto.setId(1L);
        dto.setEstado("Abierto");
        dto.setPrioridad("Media");
        MessageDTO msg = new MessageDTO();
        msg.setContenido("Mensaje inicial");
        dto.setMensajes(List.of(msg));
        given(ticketService.createTicket(any())).willReturn(dto);

        TicketRequestDTO request = new TicketRequestDTO();
        request.setUserId(1L);
        request.setUserEmail("user@example.com");
        request.setUserName("User");
        request.setAsunto("Problema");
        request.setCategoria("Consulta");
        request.setMensajeInicial("Mensaje inicial");

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.estado", is("Abierto")))
                .andExpect(jsonPath("$.mensajes[0].contenido", is("Mensaje inicial")));
    }

    @Test
    void createTicket_shouldReturn400OnValidationError() throws Exception {
        given(ticketService.createTicket(any())).willThrow(new IllegalArgumentException("El asunto es obligatorio"));

        TicketRequestDTO request = new TicketRequestDTO();
        request.setUserId(1L);
        request.setUserEmail("user@example.com");
        request.setAsunto("");
        request.setMensajeInicial("");

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTicketById_shouldReturn200WhenExists() throws Exception {
        TicketDTO dto = new TicketDTO();
        dto.setId(5L);
        dto.setEstado("Abierto");
        dto.setMensajes(Collections.emptyList());
        given(ticketService.getTicketById(5L)).willReturn(dto);

        mockMvc.perform(get("/api/tickets/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.estado", is("Abierto")));
    }

    @Test
    void getTicketById_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new EntityNotFoundException("No encontrado")).when(ticketService).getTicketById(99L);

        mockMvc.perform(get("/api/tickets/99"))
                .andExpect(status().isNotFound());
    }
}

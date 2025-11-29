package com.fixsy.mensajes;

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

import com.fixsy.mensajes.controller.MessageController;
import com.fixsy.mensajes.dto.MessageDTO;
import com.fixsy.mensajes.dto.MessageRequestDTO;
import com.fixsy.mensajes.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    private ObjectMapper objectMapper;
    private MessageDTO testMessageDTO;
    private MessageRequestDTO testMessageRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        testMessageDTO = new MessageDTO();
        testMessageDTO.setId(1L);
        testMessageDTO.setTicketId(1L);
        testMessageDTO.setSenderId(1L);
        testMessageDTO.setSenderEmail("cliente@example.com");
        testMessageDTO.setSenderName("Juan Pérez");
        testMessageDTO.setSenderRole("Usuario");
        testMessageDTO.setContenido("Mensaje de prueba");
        testMessageDTO.setIsRead(false);
        testMessageDTO.setAdjuntos(Collections.emptyList());
        testMessageDTO.setCreatedAt(LocalDateTime.now());

        testMessageRequest = new MessageRequestDTO();
        testMessageRequest.setTicketId(1L);
        testMessageRequest.setSenderId(1L);
        testMessageRequest.setSenderEmail("cliente@example.com");
        testMessageRequest.setSenderName("Juan Pérez");
        testMessageRequest.setSenderRole("Usuario");
        testMessageRequest.setContenido("Mensaje de respuesta");
    }

    @Test
    void testGetMessagesByTicketId() throws Exception {
        when(messageService.getMessagesByTicketId(1L)).thenReturn(Arrays.asList(testMessageDTO));

        mockMvc.perform(get("/api/messages/ticket/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].contenido").value("Mensaje de prueba"));
    }

    @Test
    void testSendMessage() throws Exception {
        when(messageService.sendMessage(Mockito.any(MessageRequestDTO.class))).thenReturn(testMessageDTO);

        mockMvc.perform(post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMessageRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.contenido").value("Mensaje de prueba"));
    }

    @Test
    void testMarkAsRead() throws Exception {
        doNothing().when(messageService).markMessagesAsRead(1L, 2L);

        mockMvc.perform(put("/api/messages/ticket/1/read")
                .param("readerId", "2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Mensajes marcados como leídos"));

        verify(messageService).markMessagesAsRead(1L, 2L);
    }

    @Test
    void testCountUnreadForUser() throws Exception {
        when(messageService.countUnreadForUser(1L)).thenReturn(5L);

        mockMvc.perform(get("/api/messages/unread/user/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.unreadCount").value(5));
    }

    @Test
    void testCountUnreadForSupport() throws Exception {
        when(messageService.countUnreadForSupport(2L)).thenReturn(8L);

        mockMvc.perform(get("/api/messages/unread/support/2")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.supportId").value(2))
            .andExpect(jsonPath("$.unreadCount").value(8));
    }
}


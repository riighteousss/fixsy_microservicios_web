package com.fixsy.mensajes.controller;

import com.fixsy.mensajes.dto.MessageDTO;
import com.fixsy.mensajes.dto.MessageRequestDTO;
import com.fixsy.mensajes.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
@Tag(name = "Message Controller", description = "API para gestión de mensajes en tickets")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @GetMapping("/ticket/{ticketId}")
    @Operation(summary = "Obtener mensajes de un ticket")
    public ResponseEntity<List<MessageDTO>> getMessagesByTicketId(@PathVariable Long ticketId) {
        return ResponseEntity.ok(messageService.getMessagesByTicketId(ticketId));
    }

    @PostMapping
    @Operation(summary = "Enviar mensaje en un ticket")
    public ResponseEntity<MessageDTO> sendMessage(@Valid @RequestBody MessageRequestDTO request) {
        return new ResponseEntity<>(messageService.sendMessage(request), HttpStatus.CREATED);
    }

    @PutMapping("/ticket/{ticketId}/read")
    @Operation(summary = "Marcar mensajes de un ticket como leídos")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @PathVariable Long ticketId,
            @RequestParam Long readerId) {
        messageService.markMessagesAsRead(ticketId, readerId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Mensajes marcados como leídos");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread/user/{userId}")
    @Operation(summary = "Contar mensajes no leídos para un usuario")
    public ResponseEntity<Map<String, Object>> countUnreadForUser(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("unreadCount", messageService.countUnreadForUser(userId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread/support/{supportId}")
    @Operation(summary = "Contar mensajes no leídos para un soporte")
    public ResponseEntity<Map<String, Object>> countUnreadForSupport(@PathVariable Long supportId) {
        Map<String, Object> response = new HashMap<>();
        response.put("supportId", supportId);
        response.put("unreadCount", messageService.countUnreadForSupport(supportId));
        return ResponseEntity.ok(response);
    }
}


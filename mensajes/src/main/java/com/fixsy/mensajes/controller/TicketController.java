package com.fixsy.mensajes.controller;

import com.fixsy.mensajes.dto.*;
import com.fixsy.mensajes.service.MessageService;
import com.fixsy.mensajes.service.TicketService;
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
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
@Tag(name = "Ticket Controller", description = "API para gestión de tickets de soporte")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @Autowired
    private MessageService messageService;

    @GetMapping
    @Operation(summary = "Obtener todos los tickets")
    public ResponseEntity<List<TicketDTO>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener ticket por ID (incluye mensajes)")
    public ResponseEntity<TicketDTO> getTicketById(
            @PathVariable Long id,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "userEmail", required = false) String userEmail) {
        if (userId != null || (userEmail != null && !userEmail.isBlank())) {
            return ResponseEntity.ok(ticketService.getTicketByIdForUser(id, userId, userEmail));
        }
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener tickets de un usuario")
    public ResponseEntity<List<TicketDTO>> getTicketsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(ticketService.getTicketsByUserId(userId));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Obtener tickets por email de usuario")
    public ResponseEntity<List<TicketDTO>> getTicketsByUserEmail(@PathVariable String email) {
        try {
            String decodedEmail = java.net.URLDecoder.decode(email, java.nio.charset.StandardCharsets.UTF_8);
            return ResponseEntity.ok(ticketService.getTicketsByUserEmail(decodedEmail));
        } catch (Exception e) {
            return ResponseEntity.ok(ticketService.getTicketsByUserEmail(email));
        }
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Obtener tickets por estado")
    public ResponseEntity<List<TicketDTO>> getTicketsByEstado(@PathVariable String estado) {
        return ResponseEntity.ok(ticketService.getTicketsByEstado(estado));
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Obtener tickets por categoría")
    public ResponseEntity<List<TicketDTO>> getTicketsByCategoria(@PathVariable String categoria) {
        return ResponseEntity.ok(ticketService.getTicketsByCategoria(categoria));
    }

    @GetMapping("/assigned/{supportId}")
    @Operation(summary = "Obtener tickets asignados a un soporte")
    public ResponseEntity<List<TicketDTO>> getTicketsAssignedTo(@PathVariable Long supportId) {
        return ResponseEntity.ok(ticketService.getTicketsAssignedTo(supportId));
    }

    @GetMapping("/unassigned")
    @Operation(summary = "Obtener tickets sin asignar")
    public ResponseEntity<List<TicketDTO>> getUnassignedTickets() {
        return ResponseEntity.ok(ticketService.getUnassignedTickets());
    }

    @GetMapping("/pending")
    @Operation(summary = "Obtener tickets pendientes ordenados por prioridad")
    public ResponseEntity<List<TicketDTO>> getPendingTickets() {
        return ResponseEntity.ok(ticketService.getPendingTickets());
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Obtener tickets relacionados con una orden")
    public ResponseEntity<List<TicketDTO>> getTicketsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(ticketService.getTicketsByOrderId(orderId));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo ticket (usuario logueado)")
    public ResponseEntity<TicketDTO> createTicket(@Valid @RequestBody TicketRequestDTO request) {
        return new ResponseEntity<>(ticketService.createTicket(request), HttpStatus.CREATED);
    }

    @PostMapping("/public")
    @Operation(summary = "Crear ticket publico (invitado)")
    public ResponseEntity<TicketDTO> createPublicTicket(@Valid @RequestBody PublicTicketRequestDTO request) {
        return new ResponseEntity<>(ticketService.createPublicTicket(request), HttpStatus.CREATED);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Crear ticket para recuperacion de contrasena")
    public ResponseEntity<TicketDTO> createPasswordRecoveryTicket(@Valid @RequestBody PasswordRecoveryRequestDTO request) {
        return new ResponseEntity<>(ticketService.createPasswordRecoveryTicket(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado del ticket")
    public ResponseEntity<TicketDTO> updateTicketEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        return ResponseEntity.ok(ticketService.updateTicketEstado(id, estado));
    }

    @PutMapping("/{id}/prioridad")
    @Operation(summary = "Actualizar prioridad del ticket")
    public ResponseEntity<TicketDTO> updateTicketPrioridad(
            @PathVariable Long id,
            @RequestParam String prioridad) {
        return ResponseEntity.ok(ticketService.updateTicketPrioridad(id, prioridad));
    }

    @PutMapping("/{id}/assign")
    @Operation(summary = "Asignar ticket a un soporte")
    public ResponseEntity<TicketDTO> assignTicket(
            @PathVariable Long id,
            @RequestParam Long supportId,
            @RequestParam String supportName) {
        return ResponseEntity.ok(ticketService.assignTicket(id, supportId, supportName));
    }

    @PatchMapping("/{id}/close")
    @Operation(summary = "Cerrar ticket")
    public ResponseEntity<TicketDTO> closeTicket(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.closeTicket(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ticket")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    // Estadísticas
    @GetMapping("/stats/count/{estado}")
    @Operation(summary = "Contar tickets por estado")
    public ResponseEntity<Map<String, Object>> countByEstado(@PathVariable String estado) {
        Map<String, Object> response = new HashMap<>();
        response.put("estado", estado);
        response.put("count", ticketService.countByEstado(estado));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/user/{userId}")
    @Operation(summary = "Contar tickets de un usuario")
    public ResponseEntity<Map<String, Object>> countByUserId(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("count", ticketService.countByUserId(userId));
        response.put("unread", ticketService.countUnreadForUser(userId));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/support/{supportId}")
    @Operation(summary = "Estadísticas de un soporte")
    public ResponseEntity<Map<String, Object>> statsBySupport(@PathVariable Long supportId) {
        Map<String, Object> response = new HashMap<>();
        response.put("supportId", supportId);
        response.put("assigned", ticketService.countAssignedTo(supportId));
        response.put("unread", ticketService.countUnreadForSupport(supportId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/messages")
    @Operation(summary = "Agregar mensaje a un ticket (alias)")
    public ResponseEntity<MessageDTO> addMessageToTicket(@PathVariable Long id, @Valid @RequestBody MessageRequestDTO request) {
        request.setTicketId(id);
        return new ResponseEntity<>(messageService.sendMessage(request), HttpStatus.CREATED);
    }
}

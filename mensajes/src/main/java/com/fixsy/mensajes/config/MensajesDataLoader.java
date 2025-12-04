package com.fixsy.mensajes.config;

import com.fixsy.mensajes.model.Message;
import com.fixsy.mensajes.model.Ticket;
import com.fixsy.mensajes.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Seed de tickets y mensajes para demo.
 * No crea datos si ya existen tickets en la base.
 */
@Component
@RequiredArgsConstructor
public class MensajesDataLoader implements CommandLineRunner {

    private final TicketRepository ticketRepository;

    @Override
    public void run(String... args) {
        if (ticketRepository.count() != 0) {
            return;
        }
        seedTickets();
    }

    private void seedTickets() {
        // Ticket de consulta
        Ticket consulta = createTicket(
                1L,
                "cliente@fixsy.com",
                "Carlos Perez",
                "Consulta sobre compatibilidad",
                "Consulta",
                "Abierto",
                "Media",
                null
        );
        addMessages(consulta, Arrays.asList(
                userMessage(1L, "cliente@fixsy.com", "Carlos Perez",
                        "Usuario", "Hola, este filtro sirve para un motor 1.8?"),
                supportMessage(3L, "soporte@soporte.fixsy.com", "Sofia Soporte",
                        "Soporte", "Hola Carlos, si es compatible con motores entre 1.6 y 2.0L.", true)
        ));

        // Ticket de reclamo
        Ticket reclamo = createTicket(
                1L,
                "cliente@fixsy.com",
                "Carlos Perez",
                "Reclamo por retraso de envio",
                "Reclamo",
                "En Proceso",
                "Alta",
                null
        );
        addMessages(reclamo, Arrays.asList(
                userMessage(1L, "cliente@fixsy.com", "Carlos Perez",
                        "Usuario", "Mi pedido lleva 5 dias sin movimiento, me ayudan?"),
                supportMessage(3L, "soporte@soporte.fixsy.com", "Sofia Soporte",
                        "Soporte", "Revisamos tu envio, ya esta asignado a courier y saldra hoy.", false)
        ));

        // Tickets de boleta asociados a órdenes de ejemplo
        createOrderTicket(1L, 1L,
                "Orden #1: 2x Filtro de aceite 1.6-2.0L, 1x Pastillas de freno. Total con envio $49.970. Pago Tarjeta.");
        createOrderTicket(1L, 2L,
                "Orden #2: Aceite 5W-30 y Filtro de aire. Total con envio $45.970. Pago Transferencia.");
        createOrderTicket(1L, 3L,
                "Orden #3: 2x Amortiguador delantero, 1x Liquido DOT4. Total con envio $162.970. Pago Tarjeta.");
    }

    private Ticket createTicket(Long userId,
                                String email,
                                String nombre,
                                String asunto,
                                String categoria,
                                String estado,
                                String prioridad,
                                Long orderId) {
        Ticket ticket = new Ticket();
        ticket.setUserId(userId);
        ticket.setUserEmail(email);
        ticket.setUserName(nombre);
        ticket.setAsunto(asunto);
        ticket.setCategoria(categoria);
        ticket.setEstado(estado);
        ticket.setPrioridad(prioridad);
        ticket.setOrderId(orderId);
        Ticket saved = ticketRepository.save(ticket);
        saved.setUpdatedAt(LocalDateTime.now());
        return ticketRepository.save(saved);
    }

    private void addMessages(Ticket ticket, List<Message> messages) {
        messages.forEach(ticket::addMessage);
        ticketRepository.save(ticket);
    }

    private Message userMessage(Long senderId, String senderEmail, String senderName,
                                String senderRole, String contenido) {
        Message msg = new Message();
        msg.setSenderId(senderId);
        msg.setSenderEmail(senderEmail);
        msg.setSenderName(senderName);
        msg.setSenderRole(senderRole);
        msg.setContenido(contenido);
        msg.setIsRead(false);
        return msg;
    }

    private Message supportMessage(Long senderId, String senderEmail, String senderName,
                                   String senderRole, String contenido, boolean markRead) {
        Message msg = userMessage(senderId, senderEmail, senderName, senderRole, contenido);
        msg.setIsRead(markRead);
        return msg;
    }

    private void createOrderTicket(Long userId, Long orderId, String resumenCompra) {
        Ticket ticket = createTicket(
                userId,
                "cliente@fixsy.com",
                "Carlos Perez",
                "Boleta de compra #" + orderId,
                "Boleta",
                "Abierto",
                "Media",
                orderId
        );

        Message resumen = userMessage(userId, "cliente@fixsy.com", "Carlos Perez",
                "Usuario", resumenCompra);
        resumen.setIsRead(false);

        Message respuesta = supportMessage(3L, "soporte@soporte.fixsy.com", "Sofia Soporte",
                "Soporte", "Adjuntamos tu comprobante y quedamos atentos a dudas.", true);

        addMessages(ticket, Arrays.asList(resumen, respuesta));
    }
}

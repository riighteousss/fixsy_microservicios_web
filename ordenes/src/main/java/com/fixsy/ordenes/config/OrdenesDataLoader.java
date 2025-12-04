package com.fixsy.ordenes.config;

import com.fixsy.ordenes.model.Order;
import com.fixsy.ordenes.model.OrderItem;
import com.fixsy.ordenes.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Crea ordenes de demostración si no existen.
 */
@Component
@RequiredArgsConstructor
public class OrdenesDataLoader implements CommandLineRunner {

    private final OrderRepository orderRepository;

    @Override
    public void run(String... args) {
        if (orderRepository.count() != 0) {
            return;
        }
        seedOrders();
    }

    private void seedOrders() {
        // Orden 1 - Pendiente
        createOrderWithItems(
                1L,
                "cliente@fixsy.com",
                "Carlos Pérez",
                "Metropolitana",
                "Santiago",
                "Tarjeta",
                "Pendiente",
                null,
                Arrays.asList(
                        simpleItem(1L, "Filtro de aceite 1.6-2.0L", "FLT-001", 2, 7990),
                        simpleItem(2L, "Pastillas de freno delantera", "BRK-010", 1, 29990)
                )
        );

        // Orden 2 - Pagado
        createOrderWithItems(
                1L,
                "cliente@fixsy.com",
                "Carlos Pérez",
                "Metropolitana",
                "Providencia",
                "Transferencia",
                "Pagado",
                "PAG-123456",
                Arrays.asList(
                        simpleItem(5L, "Aceite sintetico 5W-30 4L", "OIL-005", 1, 21990),
                        simpleItem(6L, "Filtro de aire panel", "FLT-015", 1, 14990)
                )
        );

        // Orden 3 - Entregado
        createOrderWithItems(
                1L,
                "cliente@fixsy.com",
                "Carlos Pérez",
                "Valparaiso",
                "Viña del Mar",
                "Tarjeta",
                "Entregado",
                "PAG-654321",
                Arrays.asList(
                        simpleItem(3L, "Amortiguador delantero gas", "SUS-020", 2, 59990),
                        simpleItem(10L, "Liquido de frenos DOT4 1L", "BRK-070", 1, 6990)
                )
        );
    }

    private OrderItem simpleItem(Long productId, String name, String sku, int qty, double unitPrice) {
        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setProductName(name);
        item.setProductSku(sku);
        item.setQuantity(qty);
        item.setOriginalUnitPrice(BigDecimal.valueOf(unitPrice));
        item.setUnitPrice(BigDecimal.valueOf(unitPrice));
        item.setDiscountUnitAmount(BigDecimal.ZERO);
        item.setSubtotal(BigDecimal.valueOf(unitPrice).multiply(BigDecimal.valueOf(qty)));
        return item;
    }

    private void createOrderWithItems(Long userId,
                                      String userEmail,
                                      String userName,
                                      String region,
                                      String comuna,
                                      String paymentMethod,
                                      String status,
                                      String paymentReference,
                                      List<OrderItem> items) {
        BigDecimal subtotal = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal shippingCost = calculateShippingCost(region);
        BigDecimal total = subtotal.add(shippingCost);

        Order order = new Order();
        order.setUserId(userId);
        order.setUserEmail(userEmail);
        order.setUserName(userName);
        order.setShippingRegion(region);
        order.setShippingComuna(comuna);
        order.setShippingAddress("Av. Demo 123, " + comuna);
        order.setContactPhone("+56911112222");
        order.setPaymentMethod(paymentMethod);
        order.setStatus(status);
        order.setSubtotal(subtotal);
        order.setShippingCost(shippingCost);
        order.setTotal(total);
        order.setPaymentReference(paymentReference);

        LocalDateTime now = LocalDateTime.now();
        if ("Pagado".equals(status) || "Enviado".equals(status) || "Entregado".equals(status)) {
            order.setPaidAt(now.minusDays(2));
        }
        if ("Enviado".equals(status) || "Entregado".equals(status)) {
            order.setShippedAt(now.minusDays(1));
            order.setTrackingNumber("TRK-" + (1000 + now.getSecond()));
        }
        if ("Entregado".equals(status)) {
            order.setDeliveredAt(now);
            order.setTrackingNumber("TRK-" + (2000 + now.getMinute()));
        }

        items.forEach(item -> item.setOrder(order));
        order.setItems(items);
        orderRepository.save(order);
    }

    private BigDecimal calculateShippingCost(String region) {
        if (region == null) {
            return BigDecimal.valueOf(5990);
        }
        switch (region.toLowerCase()) {
            case "metropolitana":
                return BigDecimal.valueOf(3990);
            case "valparaiso":
            case "o'higgins":
                return BigDecimal.valueOf(4990);
            default:
                return BigDecimal.valueOf(5990);
        }
    }
}

package com.fit.microservices.notification.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderPlacedEvent {
    private Long orderId;
    private String orderNumber;
    private Long userId;
    private List<OrderItem> items;
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderItem {
        private String skuCode;
        private Integer quantity;
        private BigDecimal price;
    }
}

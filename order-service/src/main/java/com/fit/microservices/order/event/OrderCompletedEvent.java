package com.fit.microservices.order.event;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCompletedEvent {
    private Long orderId;
    private Long userId;
    private String status;
}

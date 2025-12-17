package com.fit.microservices.notification.listener;

import com.fit.microservices.notification.event.OrderPlacedEvent;
import com.fit.microservices.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventListener {
    private final EmailService emailService;
    @KafkaListener(
            topics = "order-topic",
            groupId = "notification-service-group",
            containerFactory = "orderPlacedEventListenerFactory"
    )
    public void handleOrderEvent(OrderPlacedEvent event) {
        System.out.println("📨 Nhận được event từ Kafka: " + event);
        try {
            emailService.sendOrderEmail(event);
        } catch (Exception e) {
            System.err.println("Handle order event failed: " + e.getMessage());
        }
    }


}

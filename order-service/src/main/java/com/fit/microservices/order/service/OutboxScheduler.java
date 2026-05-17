package com.fit.microservices.order.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fit.microservices.order.event.OrderCancelEvent;
import com.fit.microservices.order.event.OrderCompletedEvent;
import com.fit.microservices.order.event.OrderPlacedEvent;
import com.fit.microservices.order.model.OutboxEvent;
import com.fit.microservices.order.producer.OrderEventProducer;
import com.fit.microservices.order.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final OrderEventProducer orderEventProducer;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    public void processOutboxEvents() {
        List<OutboxEvent> events = outboxEventRepository.findByStatus("PENDING");
        for (OutboxEvent event : events) {
            try {
                switch (event.getEventType()) {
                    case "OrderPlacedEvent":
                        OrderPlacedEvent placedEvent = objectMapper.readValue(event.getPayload(), OrderPlacedEvent.class);
                        orderEventProducer.publishOrderCreated(placedEvent);
                        break;
                    case "OrderCompletedEvent":
                        OrderCompletedEvent completedEvent = objectMapper.readValue(event.getPayload(), OrderCompletedEvent.class);
                        orderEventProducer.publishOrderCompleted(completedEvent);
                        break;
                    case "OrderCancelEvent":
                        OrderCancelEvent cancelEvent = objectMapper.readValue(event.getPayload(), OrderCancelEvent.class);
                        orderEventProducer.publishOrderCancelledEvent(cancelEvent);
                        break;
                    default:
                        System.err.println("Unknown event type: " + event.getEventType());
                }
                event.setStatus("COMPLETED");
                outboxEventRepository.save(event);
            } catch (Exception e) {
                System.err.println("Failed to process outbox event id: " + event.getId());
                e.printStackTrace();
            }
        }
    }
}

package com.fit.microservices.order.consumer;

import com.fit.microservices.order.event.PaymentCompletedEvent;
import com.fit.microservices.order.model.OrderStatus;
import com.fit.microservices.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventConsumer {
    private final OrderService orderService;
    @KafkaListener(
        topics = "payments",
        containerFactory = "paymentCompletedKafkaListenerContainerFactory"
    )
    public void handlePaymentCompleted(PaymentCompletedEvent paymentCompletedEvent) {
        System.out.println("Nhận PaymentCompletedEvent: "+paymentCompletedEvent);
        orderService.updateOrderStatus(paymentCompletedEvent.getOrderId(), OrderStatus.PENDING);
    }
}

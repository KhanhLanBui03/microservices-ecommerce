package com.fit.microservices.payment.service.Impl;

import com.fit.microservices.payment.model.InventoryReservedEvent;
import com.fit.microservices.payment.model.PaymentCompletedEvent;
import com.fit.microservices.payment.service.KafkaProducerService;
import com.fit.microservices.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final KafkaProducerService kafkaProducerService;
    @Override
    public void processPayment(InventoryReservedEvent event) {
        try{
            boolean success = Math.random() > 0.2;
            if(success){
                PaymentCompletedEvent paymentCompletedEvent = new PaymentCompletedEvent();
                paymentCompletedEvent.setOrderId(event.getOrderId());
                paymentCompletedEvent.setPaymentId(UUID.randomUUID().toString());
                paymentCompletedEvent.setAmount(100.0);
                kafkaProducerService.sendPaymentCompleted(paymentCompletedEvent);
                System.out.println("💳 Payment success -> gửi PaymentCompletedEvent");
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}

package com.fit.microservices.payment.service.Impl;

import com.fit.microservices.payment.model.InventoryReservedEvent;
import com.fit.microservices.payment.model.PaymentCompletedEvent;
import com.fit.microservices.payment.model.PaymentFailedEvent;
import com.fit.microservices.payment.producer.KafkaProducerService;
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
            }else{
                PaymentFailedEvent paymentFailedEvent = new PaymentFailedEvent();
                paymentFailedEvent.setOrderId(event.getOrderId());
                paymentFailedEvent.setReason("Insufficient funds");
                kafkaProducerService.sendPaymentFailed(paymentFailedEvent);
                System.out.println("Payment failed -> Gửi PaymentFailedEvent ");
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}

package com.fit.microservices.payment.service;

import com.fit.microservices.payment.model.PaymentCompletedEvent;
import com.fit.microservices.payment.model.PaymentFailedEvent;

public interface KafkaProducerService {
    void sendPaymentCompleted(PaymentCompletedEvent event);
    void sendPaymentFailed(PaymentFailedEvent event);
}

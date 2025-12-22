package com.fit.microservices.payment.service;

import com.fit.microservices.payment.model.InventoryReservedEvent;

public interface PaymentService {
    void processPayment(InventoryReservedEvent event);
}

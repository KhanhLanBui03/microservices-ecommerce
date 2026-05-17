package com.fit.microservices.payment.service.gateway;

import com.fit.microservices.payment.model.Payment;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Primary
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public String createPaymentUrl(Payment payment) {
        // Trả về một URL trỏ trực tiếp đến endpoint mock của PaymentController
        // Giả sử API Gateway chạy ở port 8000
        return "http://localhost:8000/api/payment/callback/mock?vnp_TxnRef=" + payment.getTransactionId();
    }

    @Override
    public boolean verifyCallback(Map<String, String> params) {
        // Luôn trả về true đối với Mock
        return true;
    }
}

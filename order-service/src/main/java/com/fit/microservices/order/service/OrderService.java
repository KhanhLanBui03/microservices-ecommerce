package com.fit.microservices.order.service;

import com.fit.microservices.order.dto.OrderRequest;

public interface OrderService {
    void placeOrder(OrderRequest orderRequest);
}

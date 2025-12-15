package com.fit.microservices.order.service;

import com.fit.microservices.order.dto.OrderRequest;
import com.fit.microservices.order.dto.OrderResponse;
import com.fit.microservices.order.model.Order;

public interface OrderService {
    void placeOrder(OrderRequest orderRequest);
    OrderResponse getOrderById(Long id);
}

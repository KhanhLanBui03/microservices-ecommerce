package com.fit.microservices.order.service.Impl;

import com.fit.microservices.order.client.InventoryClient;
import com.fit.microservices.order.client.UserClient;
import com.fit.microservices.order.dto.*;
import com.fit.microservices.order.event.OrderCancelEvent;
import com.fit.microservices.order.event.OrderCompletedEvent;
import com.fit.microservices.order.event.OrderPlacedEvent;
import com.fit.microservices.order.exception.ProductOutOfStockException;
import com.fit.microservices.order.model.Order;
import com.fit.microservices.order.model.OrderLineItem;
import com.fit.microservices.order.model.OrderStatus;
import com.fit.microservices.order.model.OutboxEvent;
import com.fit.microservices.order.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fit.microservices.order.producer.OrderEventProducer;
import com.fit.microservices.order.repository.OrderRepository;
import com.fit.microservices.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.fit.microservices.order.model.OrderStatus.PENDING;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient  inventoryClient;
    private final UserClient  userClient;
    private final OrderEventProducer orderEventProducer;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    @Override
    public String placeOrder(OrderRequest orderRequest,Long userId) {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.PENDING);
        order.setUserId(userId);
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItem>  orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setTotalPrice(orderRequest.getTotalPrice());
        order.setOrderLineItemsList(orderLineItems);
        List<String> skuCodes =order.getOrderLineItemsList().stream().map(OrderLineItem::getSkuCode).toList();
        InventoryResponse[] inventoryResponseArray = inventoryClient.checkStock(skuCodes);
        boolean allProductsInStock =  Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);
        if(!allProductsInStock){
            throw new ProductOutOfStockException("Product is not in stock");
        }
        orderRepository.save(order);
        OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(
                order.getId(),
                order.getOrderNumber(),
                order.getUserId(),
                order.getOrderLineItemsList().stream()
                        .map(item -> new OrderPlacedEvent.OrderItem(
                                item.getSkuCode(),
                                item.getQuantity(),
                                item.getPrice()
                        )).toList(),
                order.getTotalPrice()

        );

        //Gửi qua outbox
        try {
            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setAggregateId(order.getId());
            outboxEvent.setAggregateType("Order");
            outboxEvent.setEventType("OrderPlacedEvent");
            outboxEvent.setPayload(objectMapper.writeValueAsString(orderPlacedEvent));
            outboxEvent.setStatus("PENDING");
            outboxEvent.setCreatedAt(LocalDateTime.now());
            outboxEventRepository.save(outboxEvent);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save outbox event", e);
        }
        return "Order Placed Successfully";
    }
    private OrderLineItem mapToDto(OrderLineItemsDto orderLineItemDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        orderLineItem.setPrice(orderLineItemDto.getPrice());
        return orderLineItem;
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        List<OrderLineItemsDto> items = order.getOrderLineItemsList()
                .stream()
                .map(item->{
                    OrderLineItemsDto itemDto = new OrderLineItemsDto();
                    itemDto.setSkuCode(item.getSkuCode());
                    itemDto.setQuantity(item.getQuantity());
                    itemDto.setPrice(item.getPrice());
                    return itemDto;
                }).toList();
        UserResponse userResponse = userClient.getUserById(order.getUserId());
        return new OrderResponse(order.getId(),order.getOrderNumber(),items,userResponse);
    }
    private List<OrderCancelEvent.OrderItem> mapOrderItems(Order order) {
        return order.getOrderLineItemsList().stream()
                .map(item -> new OrderCancelEvent.OrderItem(
                        item.getSkuCode(),
                        item.getQuantity()
                ))
                .toList();
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setOrderStatus(status);
            Order updatedOrder = orderRepository.save(order);
            System.out.println("Đã cập nhật trạng thái đơn hàng:" +status);
            if(status == OrderStatus.COMPLETED){
                OrderCompletedEvent orderCompletedEvent = new OrderCompletedEvent(
                        updatedOrder.getId(),
                        updatedOrder.getUserId(),
                        status.name()
                );
                try {
                    OutboxEvent outboxEvent = new OutboxEvent();
                    outboxEvent.setAggregateId(updatedOrder.getId());
                    outboxEvent.setAggregateType("Order");
                    outboxEvent.setEventType("OrderCompletedEvent");
                    outboxEvent.setPayload(objectMapper.writeValueAsString(orderCompletedEvent));
                    outboxEvent.setStatus("PENDING");
                    outboxEvent.setCreatedAt(LocalDateTime.now());
                    outboxEventRepository.save(outboxEvent);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to save outbox event", e);
                }
            }
            if (status == OrderStatus.CANCELLED) {
                OrderCancelEvent event = new OrderCancelEvent(
                        updatedOrder.getId(),
                        updatedOrder.getUserId(),
                        mapOrderItems(updatedOrder),
                        "Order cancelled (payment failed)"
                );
                try {
                    OutboxEvent outboxEvent = new OutboxEvent();
                    outboxEvent.setAggregateId(updatedOrder.getId());
                    outboxEvent.setAggregateType("Order");
                    outboxEvent.setEventType("OrderCancelEvent");
                    outboxEvent.setPayload(objectMapper.writeValueAsString(event));
                    outboxEvent.setStatus("PENDING");
                    outboxEvent.setCreatedAt(LocalDateTime.now());
                    outboxEventRepository.save(outboxEvent);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to save outbox event", e);
                }
            }
        });
    }
}

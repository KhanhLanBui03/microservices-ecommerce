package com.fit.microservices.order.service.Impl;

import com.fit.microservices.order.client.InventoryClient;
import com.fit.microservices.order.client.UserClient;
import com.fit.microservices.order.dto.*;
import com.fit.microservices.order.exception.ProductOutOfStockException;
import com.fit.microservices.order.model.Order;
import com.fit.microservices.order.model.OrderLineItem;
import com.fit.microservices.order.repository.OrderRepository;
import com.fit.microservices.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
//    private final WebClient.Builder webClientBuilder;
    private final InventoryClient  inventoryClient;
    private final UserClient  userClient;
    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setUserId(orderRequest.getUserId());
        List<OrderLineItem>  orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemsList(orderLineItems);
        List<String> skuCodes =order.getOrderLineItemsList().stream().map(OrderLineItem::getSkuCode).toList();
        InventoryResponse[] inventoryResponseArray = inventoryClient.checkStock(skuCodes);
        boolean allProductsInStock =  Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);
        if(allProductsInStock){
            orderRepository.save(order);
        }else {
            throw new ProductOutOfStockException("Product is not in stock");
        }
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
}

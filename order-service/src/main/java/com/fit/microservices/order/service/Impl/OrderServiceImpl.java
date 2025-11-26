package com.fit.microservices.order.service.Impl;

import com.fit.microservices.order.dto.InventoryResponse;
import com.fit.microservices.order.dto.OrderLineItemsDto;
import com.fit.microservices.order.dto.OrderRequest;
import com.fit.microservices.order.model.Order;
import com.fit.microservices.order.model.OrderLineItem;
import com.fit.microservices.order.repository.OrderRepository;
import com.fit.microservices.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final WebClient webClient;
    @Override
    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItem>  orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemsList(orderLineItems);
        List<String> skuCodes =order.getOrderLineItemsList().stream().map(OrderLineItem::getSkuCode).toList();
        // call inventory service and place order if product is in stock
        InventoryResponse[] inventoryResponseArray =webClient.get()
                .uri("http://localhost:8083/api/inventory",uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
        boolean allProductsInStock =  Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);
        if(allProductsInStock){
            orderRepository.save(order);
        }else {
            throw new IllegalArgumentException("Product is not in stock, please try again");
        }

    }
    private OrderLineItem mapToDto(OrderLineItemsDto orderLineItemDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSkuCode(orderLineItemDto.getSkuCode());
        orderLineItem.setQuantity(orderLineItemDto.getQuantity());
        orderLineItem.setPrice(orderLineItemDto.getPrice());
        return orderLineItem;
    }
}

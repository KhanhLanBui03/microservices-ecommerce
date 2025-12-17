package com.fit.microservices.inventory.listener;


import com.fit.microservices.inventory.event.OrderPlacedEvent;
import com.fit.microservices.inventory.model.Inventory;
import com.fit.microservices.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderEventListener {
    public final InventoryRepository  inventoryRepository;
    @KafkaListener(
            topics = "order-topic",
            groupId = "inventory-service-group",
            containerFactory = "orderPlacedEventListenerFactory"
    )
    @Transactional
    public void handleOrderPlacedEvent(OrderPlacedEvent event) {

        List<String> skuCodes = event.getItems()
                .stream()
                .map(OrderPlacedEvent.OrderItem::getSkuCode)
                .toList();

        List<Inventory> inventories = inventoryRepository.findBySkuCodeIn(skuCodes);

        Map<String, Inventory> inventoryMap = inventories.stream()
                .collect(Collectors.toMap(Inventory::getSkuCode, i -> i));

        for (OrderPlacedEvent.OrderItem item : event.getItems()) {
            Inventory inventory = inventoryMap.get(item.getSkuCode());

            if (inventory.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("Out of stock: " + item.getSkuCode());
            }
            inventory.setQuantity(
                    inventory.getQuantity() - item.getQuantity()
            );
        }
    }



}

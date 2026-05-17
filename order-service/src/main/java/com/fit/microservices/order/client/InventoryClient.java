package com.fit.microservices.order.client;

import com.fit.microservices.order.dto.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "inventory-service")
public interface InventoryClient {
    @GetMapping("/api/inventory")
    @CircuitBreaker(name = "inventory", fallbackMethod = "checkStockFallback")
    InventoryResponse[] checkStock(@RequestParam("skuCode") List<String> skuCode);

    default InventoryResponse[] checkStockFallback(List<String> skuCode, Throwable throwable) {
        throw new RuntimeException("Inventory Service is currently unavailable. Please try again later.");
    }
}

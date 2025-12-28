package com.fit.microservice.mcp.client;

import com.fit.microservice.mcp.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
@FeignClient(name = "product-service",url = "http://localhost:8080")
public interface ProductClient {
    @GetMapping("/api/product")
    List<ProductResponse> getAllProducts() ;
}

package com.fit.microservices.order.client;

import com.fit.microservices.order.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/api/user/{id}")
    @CircuitBreaker(name = "user", fallbackMethod = "getUserByIdFallback")
    UserResponse getUserById(@PathVariable Long id);

    default UserResponse getUserByIdFallback(Long id, Throwable throwable) {
        throw new RuntimeException("User Service is currently unavailable. Please try again later.");
    }
}

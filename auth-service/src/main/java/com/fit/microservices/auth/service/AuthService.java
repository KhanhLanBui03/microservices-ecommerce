package com.fit.microservices.auth.service;

import com.fit.microservices.auth.dto.LoginRequest;
import com.fit.microservices.auth.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> login(LoginRequest loginRequest);
    void register(RegisterRequest registerRequest);
}

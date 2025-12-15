package com.fit.microservices.auth.service;

public interface AuthService {
    String login(String email, String password);
    void register(String email, String password);
}

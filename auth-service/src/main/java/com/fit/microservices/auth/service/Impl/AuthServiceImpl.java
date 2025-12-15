package com.fit.microservices.auth.service.Impl;

import com.fit.microservices.auth.model.Credential;
import com.fit.microservices.auth.repository.CredentialRepository;
import com.fit.microservices.auth.service.AuthService;
import com.fit.microservices.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    @Override
    public String login(String email, String password) {
        Credential credential = credentialRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(password, credential.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(email, credential.getRole());
    }
    @Override
    public void register(String email, String password) {
        Credential credential = new Credential();
        credential.setEmail(email);
        credential.setPassword(passwordEncoder.encode(password));
        credential.setRole("USER");

        credentialRepository.save(credential);
    }
}

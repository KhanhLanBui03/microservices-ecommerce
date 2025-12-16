package com.fit.microservices.user.controller;

import com.fit.microservices.user.dto.UserRequest;
import com.fit.microservices.user.dto.UserResponse;
import com.fit.microservices.user.model.User;
import com.fit.microservices.user.repository.UserRepository;
import com.fit.microservices.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id){
        return new ResponseEntity<>(userService.getUserById(id),HttpStatus.OK);
    }
    @GetMapping("/me")
    public User me(@RequestHeader("X-User-Email") String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    @PostMapping
    public UserResponse createUser(@RequestBody UserRequest request) {
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();
        userRepository.save(user);
        return new UserResponse(user.getId());
    }
}

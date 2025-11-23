package com.fit.microservices.user.service.Impl;

import com.fit.microservices.user.dto.UserResponse;
import com.fit.microservices.user.repository.UserRepository;
import com.fit.microservices.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(user.getId(),user.getName(),user.getUsername(),user.getPassword(),user.getRole()))
                .collect(Collectors.toList());
    }
}

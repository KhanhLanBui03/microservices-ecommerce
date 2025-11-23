package com.fit.microservices.user.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class UserResponse {
    private Long id;
    private String name;
    private String username;
    private String password;
    private String role;
}

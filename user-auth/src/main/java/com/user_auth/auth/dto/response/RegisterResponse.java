package com.user_auth.auth.dto.response;

import com.user_auth.users.entity.Role;

import java.time.LocalDateTime;

public class RegisterResponse {
    private Long id;
    private String userName;
    private String email;
    private String fullName;
    private Role role;
    private boolean activity;
    private LocalDateTime createdAt;
}

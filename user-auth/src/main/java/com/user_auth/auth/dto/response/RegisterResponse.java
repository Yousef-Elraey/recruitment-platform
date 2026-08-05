package com.user_auth.auth.dto.response;

import com.user_auth.entity.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RegisterResponse {
    private Long id;
    private String userName;
    private String email;
    private String fullName;
    private Role role;
    private boolean activity;
    private LocalDateTime createdAt;
}

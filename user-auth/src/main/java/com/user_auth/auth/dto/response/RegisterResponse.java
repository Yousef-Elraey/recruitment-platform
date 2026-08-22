package com.user_auth.auth.dto.response;

import com.user_auth.entity.Role;
import com.user_auth.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    private String phone;
    private String email;
    private String address;
    private String summary;
    private Status status;
    private String fullName;
    private Role role;
    private Boolean active;
    private LocalDateTime createdAt;
}

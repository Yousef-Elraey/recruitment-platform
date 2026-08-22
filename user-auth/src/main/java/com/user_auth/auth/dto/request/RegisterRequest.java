package com.user_auth.auth.dto.request;

import com.user_auth.entity.Role;
import com.user_auth.entity.Status;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RegisterRequest {
    @NotBlank(message = "userName is required")
    private String userName;

    @NotBlank(message = "phone is required")
    private String phone;

    @NotBlank(message = "email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "address is required")
    private String address;

    @NotBlank(message = "summary is required")
    private String summary;

    @NotNull(message = "status is required")
    private Status status;

    private Boolean active;

    @NotBlank(message = "password is required")
    private String password;

    @NotBlank(message = "fullName is required")
    private String fullName;

    private Role role;
}

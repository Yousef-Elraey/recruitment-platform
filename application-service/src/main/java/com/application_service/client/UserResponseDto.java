package com.application_service.client;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
@Setter
@Getter
@Accessors(chain = true)
public class UserResponseDto {
    private Long id;
    private String userName;
    private String email;
    private String fullName;
    private String role;
    private Boolean active;
    private LocalDateTime createdAt;

}

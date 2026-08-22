package com.user_auth.users.dto.response;

import com.user_auth.entity.Role;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
@Data
@Accessors(chain = true)
public class UpdateUserResponse {
    private Long id;
    private String phone;
    private String userName;
    private String email;
    private String fullName;
    private Role role;
    private Boolean active;
    private LocalDateTime updatedAt;

}

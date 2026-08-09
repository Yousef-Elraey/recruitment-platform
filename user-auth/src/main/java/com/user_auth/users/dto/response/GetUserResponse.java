package com.user_auth.users.dto.response;

import com.user_auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class GetUserResponse {
    private Long id;
    private String userName;
    private String email;
    private String fullName;
    private Role role;
    private Boolean active;
    private LocalDateTime createdAt;

}

package com.user_auth.users.dto.request;

import com.user_auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserSearchRequest {
    private Long id;
    private String userName;
    private String email;
    private String fullName;
    private Role role;
    private String password;
    private Boolean active;
}

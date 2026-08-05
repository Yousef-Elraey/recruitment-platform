package com.user_auth.users.dto.request;

import com.user_auth.entity.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateRoleRequest {
   @NotBlank(message = "role is required")
   private Role role;
}

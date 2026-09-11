package com.user_auth.users.dto.request;

import com.user_auth.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateRoleRequest {
   @NotNull(message = "role is required")
   private Role role;
}

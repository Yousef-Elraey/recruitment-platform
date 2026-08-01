package com.user_auth.auth.mapper;

import com.user_auth.auth.dto.request.RegisterRequest;
import com.user_auth.auth.dto.response.RegisterResponse;
import com.user_auth.users.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {
    User toUser(RegisterRequest request);
    RegisterResponse toRegisterResponse(User user);

}

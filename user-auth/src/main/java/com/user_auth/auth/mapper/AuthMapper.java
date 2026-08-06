package com.user_auth.auth.mapper;

import com.user_auth.auth.dto.request.LoginRequest;
import com.user_auth.auth.dto.request.RegisterRequest;
import com.user_auth.auth.dto.response.RegisterResponse;
import com.user_auth.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    User toUser(RegisterRequest request);
   User toUser(LoginRequest request);
    RegisterResponse toRegisterResponse(User user);

}

package com.user_auth.users.mapper;

import com.user_auth.users.dto.request.UpdateUserRequest;
import com.user_auth.users.dto.response.GetUserResponse;
import com.user_auth.entity.User;
import com.user_auth.users.dto.response.UpdateUserResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    GetUserResponse toGetUserResponse(User user);

    List<GetUserResponse> toGetUserResponses(List<User> users);

    User toUser(UpdateUserRequest updateUserRequest);

    UpdateUserResponse toUpdateUserResponse(User user);
}

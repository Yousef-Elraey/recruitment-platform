package com.user_auth.users.mapper;

import com.user_auth.users.dto.response.GetUserResponse;
import com.user_auth.users.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    GetUserResponse toGetUserResponse(User user);

    List<GetUserResponse> toGetUserResponses(List<User> users);
}

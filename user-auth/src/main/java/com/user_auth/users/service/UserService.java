package com.user_auth.users.service;

import com.user_auth.common.exception.RecruitmentBusinessException;
import com.user_auth.auth.dto.request.RegisterRequest;
import com.user_auth.users.dto.response.GetUserResponse;
import com.user_auth.auth.dto.response.RegisterResponse;
import com.user_auth.users.entity.User;
import com.user_auth.users.mapper.UserMapper;
import com.user_auth.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<GetUserResponse> getAllUsers() {
       List<User> userList = userRepository.findAll();
        if (userList.isEmpty()){
        throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND,"NO_USERS_FOUND","the users table is empty");
        }
       return userMapper.toGetUserResponses(userList);
    }

    public GetUserResponse getUserById(Long id) {
    return userMapper.toGetUserResponse(userRepository.findById(id)
                        .orElseThrow(()->
                                new RecruitmentBusinessException(HttpStatus.NOT_FOUND
                                ,"USER_NOT_FOUND"
                                ,"user with id ("+id+") not found")));
    }

    public RegisterResponse register(RegisterRequest request) {
        User user = userMapper.toUser(request);
        userRepository.save(user);
       return userMapper.toRegisterResponse(user);
    }
}

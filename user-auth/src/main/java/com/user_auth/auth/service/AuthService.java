package com.user_auth.auth.service;

import com.user_auth.auth.mapper.AuthMapper;
import com.user_auth.auth.dto.request.RegisterRequest;
import com.user_auth.auth.dto.response.RegisterResponse;
import com.user_auth.users.entity.User;
import com.user_auth.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthMapper authMapper;

    public RegisterResponse register(RegisterRequest request) {
        User user = authMapper.toUser(request);
        userRepository.save(user);
        return authMapper.toRegisterResponse(user);
    }

}

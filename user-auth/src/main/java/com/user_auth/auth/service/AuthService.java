package com.user_auth.auth.service;

import com.user_auth.auth.dto.request.LoginRequest;
import com.user_auth.auth.dto.response.LoginResponse;
import com.user_auth.auth.mapper.AuthMapper;
import com.user_auth.auth.dto.request.RegisterRequest;
import com.user_auth.auth.dto.response.RegisterResponse;
import com.user_auth.common.exception.RecruitmentBusinessException;
import com.user_auth.common.security.JwtService;
import com.user_auth.entity.User;
import com.user_auth.redis.service.TokenBlackListService;
import com.user_auth.users.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final AuthMapper authMapper;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenBlackListService tokenBlackListService;

    public RegisterResponse register(RegisterRequest request) {
        User user = authMapper.toUser(request);
        user.setPassword(encoder.encode(request.getPassword()));
        user.setActivity(true);
        userRepository.save(user);
        return authMapper.toRegisterResponse(user);
    }

    public LoginResponse login(LoginRequest loginRequest) {
       if (userRepository.findByEmail(loginRequest.getEmail()).isEmpty()){
           throw  new RecruitmentBusinessException(
                   HttpStatus.FORBIDDEN,"NOT_ALLOWED","email you enter ("+loginRequest.getEmail()+") not found)");
       }

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            UserDetails userDetails =
                    (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            Date expireAt = jwtService.extractExpiration(token);
            String userName = jwtService.extractUsername(token);

            LoginResponse response = new LoginResponse();
            response.setToken(token)
                    .setExpiresIn(expireAt);

            return response;
        }
        throw new RecruitmentBusinessException(HttpStatus.FORBIDDEN, "NOT_AUTHENTICATION", "you are not authenticated");

    }

    public void logout(HttpServletRequest request) {

        String authHeader =
                request.getHeader("Authorization");

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {
            throw new RecruitmentBusinessException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Token not found");
        }

        String token = authHeader.substring(7);
        Date expiration = jwtService.extractExpiration(token);
        long ttl = expiration.getTime() - System.currentTimeMillis();
        if (ttl > 0) {
            tokenBlackListService
                    .blacklistToken(
                            token,
                            ttl
                    );
        }
    }
}

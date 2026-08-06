package com.user_auth.auth.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.user_auth.auth.dto.request.LoginRequest;
import com.user_auth.auth.dto.request.RegisterRequest;
import com.user_auth.auth.dto.response.LoginResponse;
import com.user_auth.auth.dto.response.RegisterResponse;
import com.user_auth.auth.mapper.AuthMapper;
import com.user_auth.common.exception.RecruitmentBusinessException;
import com.user_auth.common.security.JwtService;
import com.user_auth.entity.User;
import com.user_auth.redis.service.TokenBlackListService;
import com.user_auth.users.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private TokenBlackListService tokenBlackListService;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("john@example.com");
    }

    @Test
    @DisplayName("register: encodes password, sets activity true, saves user, returns response")
    void register_savesUserWithEncodedPasswordAndActivityTrue() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("john@example.com");
        request.setPassword("plainPassword");

        RegisterResponse expectedResponse = new RegisterResponse();

        when(authMapper.toUser(request)).thenReturn(user);
        when(encoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(authMapper.toRegisterResponse(user)).thenReturn(expectedResponse);

        RegisterResponse result = authService.register(request);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(user.isActivity()).isTrue();
        assertEquals("encodedPassword", user.getPassword());

        verify(encoder).encode("plainPassword");
        verify(userRepository).save(user);
        verify(authMapper).toRegisterResponse(user);
    }

    @Test
    @DisplayName("login: throws FORBIDDEN when email is not found")
    void login_throwsForbidden_whenEmailNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("missing@example.com");
        request.setPassword("whatever");

        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        RecruitmentBusinessException ex = assertThrows(RecruitmentBusinessException.class,
                () -> authService.login(request));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("login: returns token and expiry when authentication succeeds")
    void login_returnsLoginResponse_whenAuthenticated() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("correctPassword");

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        Date expiresAt = new Date(System.currentTimeMillis() + 60_000);

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("generated.jwt.token");
        when(jwtService.extractExpiration("generated.jwt.token")).thenReturn(expiresAt);
        when(jwtService.extractUsername("generated.jwt.token")).thenReturn("john@example.com");

        LoginResponse result = authService.login(request);

        assertThat(result).isNotNull();
        assertEquals("generated.jwt.token", result.getToken());
        assertEquals(expiresAt, result.getExpiresIn());
    }

    @Test
    @DisplayName("login: throws FORBIDDEN when authentication is not authenticated")
    void login_throwsForbidden_whenNotAuthenticated() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("wrongPassword");

        Authentication authentication = mock(Authentication.class);

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        RecruitmentBusinessException ex = assertThrows(RecruitmentBusinessException.class,
                () -> authService.login(request));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
        verify(jwtService, never()).generateToken((UserDetails) any());
    }

    @Test
    @DisplayName("logout: throws NOT_FOUND when Authorization header is missing")
    void logout_throwsNotFound_whenHeaderMissing() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(null);

        RecruitmentBusinessException ex = assertThrows(RecruitmentBusinessException.class,
                () -> authService.logout(httpServletRequest));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        verify(tokenBlackListService, never()).blacklistToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("logout: throws NOT_FOUND when Authorization header does not start with Bearer")
    void logout_throwsNotFound_whenHeaderNotBearer() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Basic abc123");

        RecruitmentBusinessException ex = assertThrows(RecruitmentBusinessException.class,
                () -> authService.logout(httpServletRequest));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        verify(tokenBlackListService, never()).blacklistToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("logout: blacklists token when it has not yet expired")
    void logout_blacklistsToken_whenTtlPositive() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer valid.token.value");

        Date futureExpiry = new Date(System.currentTimeMillis() + 60_000);
        when(jwtService.extractExpiration("valid.token.value")).thenReturn(futureExpiry);

        authService.logout(httpServletRequest);

        verify(tokenBlackListService).blacklistToken(eq("valid.token.value"), org.mockito.ArgumentMatchers.longThat(ttl -> ttl > 0));
    }

    @Test
    @DisplayName("logout: does not blacklist token when it has already expired")
    void logout_doesNotBlacklistToken_whenTtlNotPositive() {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer expired.token.value");

        Date pastExpiry = new Date(System.currentTimeMillis() - 60_000);
        when(jwtService.extractExpiration("expired.token.value")).thenReturn(pastExpiry);

        authService.logout(httpServletRequest);

        verify(tokenBlackListService, never()).blacklistToken(anyString(), anyLong());
    }
}
package com.user_auth.users.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.user_auth.common.exception.RecruitmentBusinessException;
import com.user_auth.common.security.JwtService;
import com.user_auth.entity.Role;
import com.user_auth.entity.User;
import com.user_auth.users.dto.request.UpdateRoleRequest;
import com.user_auth.users.dto.request.UpdateUserRequest;
import com.user_auth.users.dto.request.UserSearchRequest;
import com.user_auth.users.dto.response.GetUserResponse;
import com.user_auth.users.dto.response.PageResponse;
import com.user_auth.users.dto.response.UpdateUserResponse;
import com.user_auth.users.mapper.UserMapper;
import com.user_auth.users.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

   @Mock
   private UserRepository userRepository;

   @Mock
   private UserMapper userMapper;

   @Mock
   private JwtService jwtService;

   @Mock
   private PasswordEncoder encoder;

   @InjectMocks
   private UserService userService;

   private User user;
   private GetUserResponse getUserResponse;
   private UpdateUserResponse updateUserResponse;
   private UserSearchRequest userSearchRequest;

   @BeforeEach
   void setUp() {
      user = new User();
      user.setId(1L);
      user.setUserName("john_doe");
      user.setEmail("john@example.com");
      user.setFullName("John Doe");

      getUserResponse = new GetUserResponse();
      updateUserResponse = new UpdateUserResponse();
   }


      @Test
      @DisplayName("returns a PageResponse when users exist")
      void returnsPageResponse_whenUsersExist() {
         List<User> users = List.of(user);
         Page<User> userPage = new PageImpl<>(users, Pageable.ofSize(10), 1);

         when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
         when(userMapper.toGetUserResponses(users)).thenReturn(List.of(getUserResponse));

         PageResponse<GetUserResponse> result = userService.getAllUsers(userSearchRequest, 0, 10, "id", "asc");

         assertThat(result).isNotNull();
         assertEquals(1, result.getData().size());
         assertEquals(0, result.getPage());
         assertEquals(1, result.getTotalElements());
         assertEquals(1, result.getTotalPages());
         assertThat(result.isFirst()).isTrue();
         assertThat(result.isLast()).isTrue();

         verify(userRepository).findAll(any(Pageable.class));
         verify(userMapper).toGetUserResponses(users);
      }

      @Test
      @DisplayName("throws NOT_FOUND when no users exist")
      void throwsException_whenNoUsersFound() {
         Page<User> emptyPage = new PageImpl<>(Collections.emptyList());
         when(userRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

         RecruitmentBusinessException ex = assertThrows(RecruitmentBusinessException.class,
                 () -> userService.getAllUsers(userSearchRequest, 0, 10, "id", "asc"));

         assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
         verify(userMapper, never()).toGetUserResponses(any());
      }

      @Test
      @DisplayName("uses descending sort when direction is 'desc'")
      void usesDescendingSort_whenDirectionIsDesc() {
         Page<User> userPage = new PageImpl<>(List.of(user));
         when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
         when(userMapper.toGetUserResponses(any())).thenReturn(List.of(getUserResponse));

         userService.getAllUsers(userSearchRequest, 0, 10, "id", "desc");

         verify(userRepository).findAll(argThatPageableIsDescending());
      }

      private Pageable argThatPageableIsDescending() {
         return org.mockito.ArgumentMatchers.argThat(pageable ->
                 pageable.getSort().getOrderFor("id") != null
                         && pageable.getSort().getOrderFor("id").isDescending());
      }

      @Test
      @DisplayName("returns user when found")
      void returnsUser_whenFound() {
         when(userRepository.findById(1L)).thenReturn(Optional.of(user));
         when(userMapper.toGetUserResponse(user)).thenReturn(getUserResponse);

         GetUserResponse result = userService.getUserById(1L);

         assertThat(result).isEqualTo(getUserResponse);
         verify(userRepository).findById(1L);
      }

      @Test
      @DisplayName("throws NOT_FOUND when user does not exist")
      void throwsException_whenNotFound() {
         when(userRepository.findById(99L)).thenReturn(Optional.empty());

         RecruitmentBusinessException ex = assertThrows(RecruitmentBusinessException.class,
                 () -> userService.getUserById(99L));

         assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
         verify(userMapper, never()).toGetUserResponse(any());
      }

      @Test
      @DisplayName("updates fields, encodes password and saves user")
      void updatesUser_whenExists() {
         UpdateUserRequest request = new UpdateUserRequest();
         request.setUserName("new_name");
         request.setEmail("new@example.com");
         request.setPassword("plainPassword");
         request.setFullName("New Name");
         request.setRole(Role.ADMIN);

         when(userRepository.findById(1L)).thenReturn(Optional.of(user));
         when(encoder.encode("plainPassword")).thenReturn("encodedPassword");
         when(userMapper.toUpdateUserResponse(user)).thenReturn(updateUserResponse);

         UpdateUserResponse result = userService.updateUserData(1L, request);

         assertThat(result).isEqualTo(updateUserResponse);
         assertEquals("new_name", user.getUserName());
         assertEquals("new@example.com", user.getEmail());
         assertEquals("encodedPassword", user.getPassword());
         assertEquals("New Name", user.getFullName());
         assertEquals(Role.ADMIN, user.getRole());
         assertThat(user.getActive()).isTrue();

         verify(encoder).encode("plainPassword");
         verify(userRepository).save(user);
      }

      @Test
      @DisplayName("throws NOT_FOUND when user does not exist")
      void throwsException_whenUpdateUserAndUserNotFound() {
         when(userRepository.findById(1L)).thenReturn(Optional.empty());
         UpdateUserRequest request = new UpdateUserRequest();

         RecruitmentBusinessException ex = assertThrows(RecruitmentBusinessException.class,
                 () -> userService.updateUserData(1L, request));

         assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
         verify(userRepository, never()).save(any());
      }

      @Test
      @DisplayName("updates role and saves user")
      void updatesRole_whenExists() {
         UpdateRoleRequest request = new UpdateRoleRequest();
         request.setRole(Role.HR);

         when(userRepository.findById(1L)).thenReturn(Optional.of(user));
         when(userMapper.toUpdateUserResponse(user)).thenReturn(updateUserResponse);

         UpdateUserResponse result = userService.updateUserRole(1L, request);

         assertThat(result).isEqualTo(updateUserResponse);
         assertEquals(Role.HR, user.getRole());
         verify(userRepository).save(user);
      }

      @Test
      @DisplayName("throws NOT_FOUND when user does not exist")
      void throwsException_whenUpdateRoleAndUserNotFound() {
         when(userRepository.findById(1L)).thenReturn(Optional.empty());
         UpdateRoleRequest request = new UpdateRoleRequest();

         RecruitmentBusinessException ex = assertThrows(RecruitmentBusinessException.class,
                 () -> userService.updateUserRole(1L, request));

         assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
         verify(userRepository, never()).save(any());
      }

      @Mock
      private HttpServletRequest httpServletRequest;

      @Test
      @DisplayName("returns current user when token is valid")
      void returnsUser_whenTokenValid() {
         when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer valid.token.value");
         when(jwtService.extractUsername("valid.token.value")).thenReturn("john@example.com");
         when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
         when(userMapper.toGetUserResponse(user)).thenReturn(getUserResponse);

         GetUserResponse result = userService.getCurrentUser(httpServletRequest);

         assertThat(result).isEqualTo(getUserResponse);
      }

      @Test
      @DisplayName("throws FORBIDDEN when Authorization header is missing")
      void throwsForbidden_whenHeaderMissing() {
         when(httpServletRequest.getHeader("Authorization")).thenReturn(null);

         RecruitmentBusinessException ex = assertThrows(RecruitmentBusinessException.class,
                 () -> userService.getCurrentUser(httpServletRequest));

         assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
      }

      @Test
      @DisplayName("throws FORBIDDEN when Authorization header does not start with Bearer")
      void throwsForbidden_whenHeaderNotBearer() {
         when(httpServletRequest.getHeader("Authorization")).thenReturn("Basic abc123");

         RecruitmentBusinessException ex = assertThrows(RecruitmentBusinessException.class,
                 () -> userService.getCurrentUser(httpServletRequest));

         assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
      }

      @Test
      @DisplayName("throws NOT_FOUND when user for email does not exist")
      void throwsNotFound_whenUserByEmailMissing() {
         when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer valid.token.value");
         when(jwtService.extractUsername("valid.token.value")).thenReturn("missing@example.com");
         when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

         RecruitmentBusinessException ex = assertThrows(RecruitmentBusinessException.class,
                 () -> userService.getCurrentUser(httpServletRequest));

         assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
      }
      @Test
      @DisplayName("sets activity to false and saves user when found")
      void deactivatesUser_whenExists() {
         user.setActive(true);
         when(userRepository.findById(1L)).thenReturn(Optional.of(user));

         userService.deleteUser(1L);

         assertThat(user.getActive()).isFalse();
         verify(userRepository).save(user);
      }

      @Test
      @DisplayName("throws NOT_FOUND when user does not exist")
      void throwsException_whenUserNotFound() {
         when(userRepository.findById(1L)).thenReturn(Optional.empty());

         RecruitmentBusinessException ex = assertThrows(RecruitmentBusinessException.class,
                 () -> userService.deleteUser(1L));

         assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
         verify(userRepository, never()).save(any());
      }
   }
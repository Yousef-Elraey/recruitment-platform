package com.user_auth.users.service;

import com.user_auth.common.exception.RecruitmentBusinessException;
import com.user_auth.common.security.JwtService;
import com.user_auth.users.dto.request.UpdateRoleRequest;
import com.user_auth.users.dto.request.UpdateUserRequest;
import com.user_auth.users.dto.request.UserSearchRequest;
import com.user_auth.users.dto.response.GetUserResponse;
import com.user_auth.entity.User;
import com.user_auth.users.dto.response.PageResponse;
import com.user_auth.users.dto.response.UpdateUserResponse;
import com.user_auth.users.mapper.UserMapper;
import com.user_auth.users.repository.UserRepository;
import com.user_auth.users.specification.UserSpecification;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;

    public PageResponse<GetUserResponse> getAllUsers(UserSearchRequest userSearchRequest, int page, int size, String sortBy, String direction) {



        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<User> specification = Specification.unrestricted();
        specification = specification
                .and(UserSpecification.hasPhone(userSearchRequest.getPhone()))
                .and(UserSpecification.hasId(userSearchRequest.getId()))
                .and(UserSpecification.hasUserName(userSearchRequest.getUserName()))
                .and(UserSpecification.hasEmail(userSearchRequest.getEmail()))
                .and(UserSpecification.hasFullName(userSearchRequest.getFullName()))
                .and(UserSpecification.hasRole(userSearchRequest.getRole()))
                .and(UserSpecification.hasPassword(userSearchRequest.getPassword()))
                .and(UserSpecification.hasActive(userSearchRequest.getActive()));

        Page<User> userPage = userRepository.findAll(specification,pageable);

        if (userPage.isEmpty()){
            return PageResponse.<GetUserResponse>builder()
                    .data(new ArrayList<>())
                    .page(userPage.getNumber())
                    .size(userPage.getSize())
                    .totalElements(userPage.getTotalElements())
                    .totalPages(userPage.getTotalPages())
                    .first(userPage.isFirst())
                    .last(userPage.isLast())
                    .build();
        }
        List<User> userList = userPage.getContent();
        List<GetUserResponse> response = userMapper.toGetUserResponses(userList);

        return PageResponse.<GetUserResponse>builder()
                .data(response)
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .first(userPage.isFirst())
                .last(userPage.isLast())
                .build();
    }

    public GetUserResponse getUserById(Long id) {

        return userMapper.toGetUserResponse(userRepository.findById(id)
                .orElseThrow(() ->
                        new RecruitmentBusinessException(HttpStatus.NOT_FOUND
                                , "USER_NOT_FOUND"
                                , "user with id (" + id + ") not found")));
    }

    public UpdateUserResponse updateUserData(Long id, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new RecruitmentBusinessException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "user with id (" + id + ") enter not found"));

        user.setPhone(updateUserRequest.getPhone())
                .setUserName(updateUserRequest.getUserName())
                .setEmail(updateUserRequest.getEmail())
                .setPassword(encoder.encode(updateUserRequest.getPassword()))
                .setFullName(updateUserRequest.getFullName())
                .setRole(updateUserRequest.getRole())
                .setActive(true)
                .setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return userMapper.toUpdateUserResponse(user);
    }

    public UpdateUserResponse updateUserRole(Long id, UpdateRoleRequest request) {
       User user = userRepository.findById(id).orElseThrow(()->
                new RecruitmentBusinessException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "user with id (" + id + ") not found"));
            user.setRole(request.getRole());
       userRepository.save(user);
       return userMapper.toUpdateUserResponse(user);
    }

    public GetUserResponse getCurrentUser(HttpServletRequest request) {
         String authHeader = request.getHeader("Authorization");
         if (authHeader == null || !authHeader.startsWith("Bearer "))
             throw new RecruitmentBusinessException(HttpStatus.FORBIDDEN,"Not_Allowed","this token is not allowed");

         String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);
       User currentUser = userRepository.findByEmail(email).orElseThrow(()->
               new RecruitmentBusinessException(HttpStatus.NOT_FOUND,"NOT_FOUND","user with email( "+email+" )is not found"));

       return userMapper.toGetUserResponse(currentUser);
    }

    public void deleteUser(Long id) {
       User user = userRepository.findById(id).orElseThrow(()->
                new RecruitmentBusinessException(HttpStatus.NOT_FOUND,"NOT_FOUND","user with id (\" + id + \") not found"));
        user.setActive(false);
        userRepository.save(user);
    }
}

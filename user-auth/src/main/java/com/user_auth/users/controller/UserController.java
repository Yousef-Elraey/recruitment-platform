package com.user_auth.users.controller;

import com.user_auth.users.dto.request.UpdateRoleRequest;
import com.user_auth.users.dto.request.UpdateUserRequest;
import com.user_auth.users.dto.response.GetUserResponse;
import com.user_auth.users.dto.response.PageResponse;
import com.user_auth.users.dto.response.UpdateUserResponse;
import com.user_auth.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "User APIs")
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Return all users")
    public ResponseEntity<PageResponse<GetUserResponse>> getAllUsers(HttpServletRequest request,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size,
                                                                     @RequestParam(defaultValue = "id") String sortBy,
                                                                     @RequestParam(defaultValue = "asc") String direction) {
        return new ResponseEntity<>(userService.getAllUsers(page, size, sortBy, direction), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Return user by id")
    public ResponseEntity<GetUserResponse> getUserById(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Edit user data")
    public ResponseEntity<UpdateUserResponse> updateUserData(@PathVariable Long id,@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        return new ResponseEntity<UpdateUserResponse>(userService.updateUserData(id, updateUserRequest), HttpStatus.OK);
    }
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user role")
    public ResponseEntity<UpdateUserResponse> updateUserRole(@PathVariable Long id,@Valid @RequestBody UpdateRoleRequest request){
        return new ResponseEntity<>(userService.updateUserRole(id, request),HttpStatus.OK);
    }
    @GetMapping("/me")
    @Operation(summary = "Return the current authenticated user")
    public ResponseEntity<GetUserResponse> getCurrentUser(HttpServletRequest request){
        return new ResponseEntity<>(userService.getCurrentUser(request),HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return new ResponseEntity<>("User de-Activated",HttpStatus.NO_CONTENT);
    }

}
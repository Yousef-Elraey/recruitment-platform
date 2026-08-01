package com.user_auth.users.controller;

import com.user_auth.users.dto.response.GetUserResponse;
import com.user_auth.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class userController {
  private final UserService userService;
    @GetMapping
    public ResponseEntity<List<GetUserResponse>> getAllUsers(){
        return new ResponseEntity<>( userService.getAllUsers(), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<GetUserResponse> getUserById(@PathVariable Long id){
        return new ResponseEntity<>(userService.getUserById(id),HttpStatus.OK);
    }

}

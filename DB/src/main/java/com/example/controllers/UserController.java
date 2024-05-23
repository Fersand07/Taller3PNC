package com.example.controllers;

import com.example.controllers.DTOS.AuthenticatedUserRequest;
import com.example.controllers.DTOS.RegisterUserRequestDTO;
import com.example.model.UserModel;
import com.example.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserModel> registerUser(@Validated @RequestBody RegisterUserRequestDTO userRequest){
        UserModel registeredUser = userService.registerUser(userRequest.getLogin(), userRequest.getPassword(), userRequest.getEmail());
        if (registeredUser == null){
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserModel> authenticateUser(@Validated @RequestBody AuthenticatedUserRequest userRequest) {
        UserModel authenticatedUser = userService.authenticate(userRequest.getLogin(), userRequest.getPassword());
        if (authenticatedUser == null) {
            return ResponseEntity.status(401).body(null);
        }
        return ResponseEntity.ok(authenticatedUser);
    }
}

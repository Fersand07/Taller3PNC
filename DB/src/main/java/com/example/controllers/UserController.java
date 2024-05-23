package com.example.controllers;

import com.example.controllers.DTOS.AuthenticatedUserRequest;
import com.example.controllers.DTOS.RegisterUserRequestDTO;
import com.example.controllers.DTOS.TokenDTO;
import com.example.entity.Token;
import com.example.model.User;
import com.example.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @Autowired
    public UserController(UserServiceImpl userServiceImpl){
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Validated @RequestBody RegisterUserRequestDTO userRequest){
        User registeredUser = userServiceImpl.registerUser(userRequest.getLogin(), userRequest.getPassword(), userRequest.getEmail());
        if (registeredUser == null){
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> authenticateUser(@Validated @RequestBody AuthenticatedUserRequest userRequest) {
        User authenticatedUser = userServiceImpl.authenticate(userRequest.getLogin(), userRequest.getPassword());
        if (authenticatedUser == null) {
            return ResponseEntity.status(401).body(null);
        }
        try {
            Token token = userServiceImpl.registerToken(userRequest);
            return new ResponseEntity<>(new TokenDTO(token), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package com.example.services;

import com.example.entity.Token;
import com.example.entity.User;

public interface UserService {
    User findOneByIdentifier(String identifier);
    Token registerToken(User user) throws Exception;
    Boolean isTokenValid(User user, String token);
    void cleanTokens(User user) throws Exception;
}

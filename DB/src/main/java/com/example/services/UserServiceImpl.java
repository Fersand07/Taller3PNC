package com.example.services;

import com.example.controllers.DTOS.AuthenticatedUserRequest;
import com.example.entity.Token;
import com.example.entity.User;
import com.example.repositories.TokenRepository;
import com.example.repositories.UserRepository;
import com.example.utils.JWTTools;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    private JWTTools jwtTools;

    @Autowired
    private TokenRepository tokenRepository;

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User registerUser(String login, String password, String email){
        if(login == null || password == null){
            return null;
        } else {
            User user = new User();
            user.setLogin(login);
            user.setPassword(password);
            user.setEmail(email);
            return userRepository.save(user);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with login: " + login));
    }

    public User authenticate(String login, String password){
        return userRepository.findByLoginAndPassword(login, password).orElse(null);
    }

    private User convertToUser(AuthenticatedUserRequest userRequest){
        User user = new User();
        user.setLogin(userRequest.getLogin());
        user.setPassword(userRequest.getPassword());
        return user;
    }

    @Transactional(rollbackOn = Exception.class)
    public Token registerToken(AuthenticatedUserRequest userRequest) throws Exception {
        cleanTokens(userRequest);

        User user = convertToUser(userRequest);

        String tokenString = jwtTools.generateToken(userRequest);
        Token token = new Token(tokenString, user);

        tokenRepository.save(token);

        return token;
    }

    public Boolean isTokenValid(User user, String token) {
        try {
            AuthenticatedUserRequest userRequest = convertToUserRequest(user);
            cleanTokens(userRequest);
            List<Token> tokens = tokenRepository.findByUserAndActive(user, true);

            tokens.stream()
                    .filter(tk -> tk.getContent().equals(token))
                    .findAny()
                    .orElseThrow(() -> new Exception());

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private AuthenticatedUserRequest convertToUserRequest(User user) {
        AuthenticatedUserRequest userRequest = new AuthenticatedUserRequest();
        // Aquí asigna los atributos necesarios de User a AuthenticatedUserRequest
        return userRequest;
    }

    @Transactional(rollbackOn = Exception.class)
    public void cleanTokens(AuthenticatedUserRequest userRequest) throws Exception {
        List<Token> tokens = tokenRepository.findByUserAndActive(convertToUser(userRequest), true);

        tokens.forEach(token -> {
            if(!jwtTools.verifyToken(token.getContent())) {
                token.setActive(false);
                tokenRepository.save(token);
            }
        });
    }
}

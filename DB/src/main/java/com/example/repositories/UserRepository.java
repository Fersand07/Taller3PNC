package com.example.repositories;

import com.example.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Integer> {
    Optional<UserModel> findByLogin(String login);
    Optional<UserModel> findByLoginAndPassword(String login, String password);
}

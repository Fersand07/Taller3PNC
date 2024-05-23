package com.example.repositories;

import com.example.entity.Token;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID>{

    List<Token> findByActiveTrue(User user, boolean active);

    List<Token> findByUserAndActive(User user, boolean b);
}

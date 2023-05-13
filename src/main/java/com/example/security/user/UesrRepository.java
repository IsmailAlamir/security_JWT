package com.example.security.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UesrRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

}

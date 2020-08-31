package com.aditya.repository;

import com.aditya.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByUsername(String name);

    Boolean existsByEmail(String email);

    Optional<User> findByUsername(String name);
}

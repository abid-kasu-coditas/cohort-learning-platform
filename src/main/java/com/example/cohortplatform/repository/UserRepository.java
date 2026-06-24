package com.example.cohortplatform.repository;

import com.example.cohortplatform.entities.User;
import com.example.cohortplatform.entities.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByUsername(@NotBlank String username);

    boolean existsByEmail(@NotBlank @Email String email);

    List<User> findAllByRole(UserRole role);

    Page<User> findAllByRole(UserRole role, Pageable pageable);

    long countByRole(UserRole role);
}
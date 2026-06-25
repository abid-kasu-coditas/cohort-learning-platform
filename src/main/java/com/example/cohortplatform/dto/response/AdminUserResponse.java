package com.example.cohortplatform.dto.response;

import com.example.cohortplatform.entities.enums.UserRole;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AdminUserResponse(
        Long id,
        String firstName,
        String lastName,
        String username,
        String email,
        UserRole role,
        boolean isActive,
        LocalDateTime createdAt
) {}

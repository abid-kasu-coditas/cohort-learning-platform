package com.example.cohortplatform.dto.response;

public record RegisterResponse(
        String email,
        String role,
        String username
) {
}

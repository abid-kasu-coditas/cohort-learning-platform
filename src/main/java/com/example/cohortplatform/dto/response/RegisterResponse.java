package com.example.cohortplatform.dto.response;

import lombok.Builder;

@Builder
public record RegisterResponse(
        String email,
        String role,
        String username
) {
}

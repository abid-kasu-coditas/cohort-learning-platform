package com.example.cohortplatform.dto.response;

import lombok.Builder;

@Builder
public record AuthResponse(
         Long userId,
         String accessToken,
         String refreshToken,
         String role,
         String email
) {
}

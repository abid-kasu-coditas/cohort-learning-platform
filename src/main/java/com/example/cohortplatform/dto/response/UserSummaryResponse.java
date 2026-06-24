package com.example.cohortplatform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponse {
    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String role;
    private boolean isActive;
    private LocalDateTime createdAt;
}

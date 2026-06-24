package com.example.cohortplatform.dto.request;

import com.example.cohortplatform.entities.enums.RegisterRole;
import com.example.cohortplatform.entities.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterUserRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank @Email String email,
        @NotNull UserRole role
 ) {
}

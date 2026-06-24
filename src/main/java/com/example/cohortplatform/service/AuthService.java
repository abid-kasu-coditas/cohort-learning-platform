package com.example.cohortplatform.service;

import com.example.cohortplatform.dto.request.RefreshTokenRequest;
import com.example.cohortplatform.dto.request.RegisterUserRequest;
import com.example.cohortplatform.dto.response.AuthResponse;
import com.example.cohortplatform.dto.response.LoginRequest;
import com.example.cohortplatform.dto.response.RegisterResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    public RegisterResponse registerUser(RegisterUserRequest request) {
    }

    public AuthResponse login(@Valid LoginRequest request) {
    }

    public void logout(Long id) {

    }

    public AuthResponse refresh(@Valid RefreshTokenRequest request) {
    }
}

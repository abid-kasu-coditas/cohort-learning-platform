package com.example.cohortplatform.controller;

import com.example.cohortplatform.dto.request.RefreshTokenRequest;
import com.example.cohortplatform.dto.request.RegisterUserRequest;
import com.example.cohortplatform.dto.response.*;
import com.example.cohortplatform.security.UserPrincipal;
import com.example.cohortplatform.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController{

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterUserRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(authService.registerUser(request),"User Registered Successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(ApiResponse.success(authService.login(request),"Login Successful"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserPrincipal principal){
        authService.logout(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Logout Successfully"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request){
        return ResponseEntity.ok(ApiResponse.success(authService.refresh(request),"Token Refreshed"));
    }

}

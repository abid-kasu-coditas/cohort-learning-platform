package com.example.cohortplatform.service;

import com.example.cohortplatform.dto.request.LoginRequest;
import com.example.cohortplatform.dto.request.RefreshTokenRequest;
import com.example.cohortplatform.dto.request.RegisterUserRequest;
import com.example.cohortplatform.dto.response.AuthResponse;
import com.example.cohortplatform.dto.response.RegisterResponse;
import com.example.cohortplatform.entities.RefreshToken;
import com.example.cohortplatform.entities.User;
import com.example.cohortplatform.entities.enums.UserRole;
import com.example.cohortplatform.exception.EmailAlreadyExistsException;
import com.example.cohortplatform.exception.InvalidTokenException;
import com.example.cohortplatform.exception.UnauthorizedException;
import com.example.cohortplatform.exception.UsernameAlreadyExistsException;
import com.example.cohortplatform.repository.RefreshTokenRepository;
import com.example.cohortplatform.repository.UserRepository;
import com.example.cohortplatform.security.JwtTokenProvider;
import com.example.cohortplatform.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Value("${app.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;


    public RegisterResponse registerUser(RegisterUserRequest request) {
        log.info("Registering new user: username={}, email={}, role={}", request.username(), request.email(), request.role());
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException("Username already taken.");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already taken.");
        }
        if (request.role() == UserRole.SUPER_ADMIN) {
            throw new UnauthorizedException("Incorrect Role");
        }

        User user = User.builder()
                .firstname(request.firstName())
                .lastname(request.lastName())
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);
        log.info("User registered successfully: username={}", user.getUsername());

        return RegisterResponse.builder()
                .email(user.getEmail())
                .role(user.getRole().name())
                .username(user.getUsername())
                .build();

    }


    @Transactional
    public AuthResponse login(@Valid LoginRequest request) {
        log.info("Login attempt for email: {}", request.email());
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();

        String accessToken = tokenProvider.generateAccessToken(auth);
        String refreshToken = tokenProvider.generateRefreshToken(principal);

        refreshTokenRepository.revokeAllByUserId(principal.getId());

        RefreshToken rt = RefreshToken.builder()
                .token(refreshToken)
                .user(userRepository.getReferenceById(principal.getId()))
                .expiresAt(LocalDateTime.now().plusSeconds((refreshTokenExpiration / 1000)))
                .build();

        refreshTokenRepository.save(rt);
        log.info("Login successful for userId={}, role={}", principal.getId(), principal.getRole());

        return AuthResponse.builder()
                .accessToken(accessToken).refreshToken(refreshToken)
                .role(principal.getRole().name()).userId(principal.getId())
                .email(principal.getEmail())
                .build();


    }

    @Transactional
    public void logout(Long userId) {
        log.info("Logging out userId={}", userId);
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    @Transactional
    public AuthResponse refresh(@Valid RefreshTokenRequest request) {
        log.debug("Refreshing access token");
        RefreshToken stored = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));
        if (stored.isRevoked()) throw new InvalidTokenException("Refresh token has been revoked");
        if (stored.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new InvalidTokenException("Refresh token has expired");

        UserPrincipal principal = UserPrincipal.create(stored.getUser());
        String newAccess = tokenProvider.generateAccessTokenFromUser(principal);
        log.info("Token refreshed for userId={}", principal.getId());

        return AuthResponse.builder()
                .accessToken(newAccess).refreshToken(stored.getToken())
                .role(principal.getRole().name()).userId(principal.getId())
                .email(principal.getEmail())
                .build();
    }
}

package com.example.cohortplatform.controller;

import com.example.cohortplatform.dto.request.CreateSessionRequest;
import com.example.cohortplatform.dto.response.ApiResponse;
import com.example.cohortplatform.dto.response.ChatMessageResponse;
import com.example.cohortplatform.dto.response.PageResponse;
import com.example.cohortplatform.dto.response.SessionResponse;

import java.util.List;
import com.example.cohortplatform.security.UserPrincipal;
import com.example.cohortplatform.service.LiveSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LiveSessionController {

    private final LiveSessionService liveSessionService;


    @PostMapping("/api/v1/instructor/courses/{courseId}/sessions")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<SessionResponse>> createSession(
            @PathVariable Long courseId,
            @Valid @RequestBody CreateSessionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        log.debug("POST /instructor/courses/{}/sessions - instructorId: {}", courseId, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                liveSessionService.createSession(courseId, principal.getId(), request), "Session created"));
    }

    @PutMapping("/api/v1/instructor/sessions/{sessionId}/start")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<SessionResponse>> startSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserPrincipal principal) {
         return ResponseEntity.ok(ApiResponse.success(
                liveSessionService.startSession(sessionId, principal.getId()), "Session started"));
    }

    @PutMapping("/api/v1/instructor/sessions/{sessionId}/end")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<SessionResponse>> endSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserPrincipal principal) {
         return ResponseEntity.ok(ApiResponse.success(
                liveSessionService.endSession(sessionId, principal.getId()), "Session ended"));
    }

}

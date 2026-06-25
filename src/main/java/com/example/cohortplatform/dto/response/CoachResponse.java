package com.example.cohortplatform.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record CoachResponse(
        String reply,
        List<ActionPerformed> actionsPerformed
) {}

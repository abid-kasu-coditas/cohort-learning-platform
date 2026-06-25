package com.example.cohortplatform.dto.response;

import lombok.Builder;

@Builder
public record UserSummaryDto(
        Long id,
        String firstName,
        String lastName,
        String email
) {}

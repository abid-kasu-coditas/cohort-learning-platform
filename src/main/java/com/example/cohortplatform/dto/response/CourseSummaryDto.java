package com.example.cohortplatform.dto.response;

import lombok.Builder;

@Builder
public record CourseSummaryDto(
        Long id,
        String title,
        String description
) {}

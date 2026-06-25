package com.example.cohortplatform.dto.response;

import lombok.Builder;

@Builder
public record CourseDetailDto(
        Long id,
        String title,
        String description,
        Integer maxCapacity,
        boolean isActive,
        UserSummaryDto instructor
) {
}

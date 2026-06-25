package com.example.cohortplatform.mapper;

import com.example.cohortplatform.dto.response.CourseDetailDto;
import com.example.cohortplatform.dto.response.CourseSummaryDto;
import com.example.cohortplatform.entities.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseMapper {

    private final UserMapper userMapper;

    public CourseSummaryDto toSummary(Course course) {
        return CourseSummaryDto.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .build();
    }

    public CourseDetailDto toDetail(Course course) {
        return CourseDetailDto.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .maxCapacity(course.getMaxCapacity())
                .isActive(course.isActive())
                .instructor(course.getInstructor() != null ? userMapper.toSummary(course.getInstructor()) : null)
                .build();
    }
}

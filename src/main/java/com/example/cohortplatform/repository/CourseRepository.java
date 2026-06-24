package com.example.cohortplatform.repository;

import com.example.cohortplatform.entities.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Page<Course> findAll(Pageable pageable);

    List<Course> findAllByInstructor_Id(Long instructorId);

    long countByIsActive(boolean isActive);
}
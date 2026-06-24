package com.example.cohortplatform.repository;

import com.example.cohortplatform.entities.CourseEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    List<CourseEnrollment> findAllByCourse_Id(Long courseId);

    List<CourseEnrollment> findAllByUser_Id(Long userId);

    boolean existsByCourse_IdAndUser_Id(Long courseId, Long userId);

    long countByCourse_Id(Long courseId);

    Page<CourseEnrollment> findAll(Pageable pageable);
}
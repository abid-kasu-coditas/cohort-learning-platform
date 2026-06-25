package com.example.cohortplatform.repository;

import com.example.cohortplatform.entities.CourseEnrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    boolean existsByCourse_IdAndUser_Id(Long courseId, Long userId);

    long countByCourse_Id(Long courseId);

    @Query("SELECT e FROM CourseEnrollment e JOIN FETCH e.user JOIN FETCH e.course c LEFT JOIN FETCH c.instructor WHERE e.course.id = :courseId")
    List<CourseEnrollment> findAllByCourse_Id(Long courseId);

    @Query("SELECT e FROM CourseEnrollment e JOIN FETCH e.user JOIN FETCH e.course c JOIN FETCH c.instructor WHERE e.user.id = :userId")
    List<CourseEnrollment> findAllByUser_Id(Long userId);

    @Query(value = "SELECT e FROM CourseEnrollment e JOIN FETCH e.user JOIN FETCH e.course c LEFT JOIN FETCH c.instructor",
            countQuery = "SELECT COUNT(e) FROM CourseEnrollment e")
    Page<CourseEnrollment> findAllWithDetails(Pageable pageable);

    @Query(value = "SELECT e FROM CourseEnrollment e JOIN FETCH e.user JOIN FETCH e.course c LEFT JOIN FETCH c.instructor WHERE e.course.id = :courseId",
            countQuery = "SELECT COUNT(e) FROM CourseEnrollment e WHERE e.course.id = :courseId")
    Page<CourseEnrollment> findPageByCourse_Id(Long courseId, Pageable pageable);

    @Query(value = "SELECT e FROM CourseEnrollment e JOIN FETCH e.user JOIN FETCH e.course c LEFT JOIN FETCH c.instructor WHERE e.user.id = :userId",
            countQuery = "SELECT COUNT(e) FROM CourseEnrollment e WHERE e.user.id = :userId")
    Page<CourseEnrollment> findPageByUser_Id(Long userId, Pageable pageable);
}
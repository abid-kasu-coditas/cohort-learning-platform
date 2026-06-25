package com.example.cohortplatform.repository;

import com.example.cohortplatform.entities.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.instructor")
    List<Course> findAllWithInstructor();

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.instructor")
    Page<Course> findAllWithInstructor(Pageable pageable);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.instructor WHERE c.id = :id")
    Optional<Course> findByIdWithInstructor(Long id);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.instructor WHERE c.instructor.id = :instructorId")
    List<Course> findAllByInstructor_Id(Long instructorId);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.instructor WHERE c.isActive = true")
    List<Course> findAllActive();

    @Query(value = "SELECT c FROM Course c LEFT JOIN FETCH c.instructor WHERE c.isActive = true",
            countQuery = "SELECT COUNT(c) FROM Course c WHERE c.isActive = true")
    Page<Course> findAllActive(Pageable pageable);

    @Query(value = "SELECT c FROM Course c LEFT JOIN FETCH c.instructor WHERE c.instructor.id = :instructorId",
            countQuery = "SELECT COUNT(c) FROM Course c WHERE c.instructor.id = :instructorId")
    Page<Course> findPageByInstructor_Id(Long instructorId, Pageable pageable);

    long countByIsActive(boolean isActive);
}
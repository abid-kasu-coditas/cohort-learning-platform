package com.example.cohortplatform.repository;

import com.example.cohortplatform.entities.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    @Query("SELECT a FROM Assignment a JOIN FETCH a.course c JOIN FETCH c.instructor WHERE a.course.id = :courseId ORDER BY a.deadline ASC")
    List<Assignment> findByCourse_Id(Long courseId);

    @Query(value = "SELECT a FROM Assignment a JOIN FETCH a.course c JOIN FETCH c.instructor WHERE a.course.id = :courseId",
            countQuery = "SELECT COUNT(a) FROM Assignment a WHERE a.course.id = :courseId")
    Page<Assignment> findPageByCourse_Id(Long courseId, Pageable pageable);
}
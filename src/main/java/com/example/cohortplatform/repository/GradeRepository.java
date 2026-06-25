package com.example.cohortplatform.repository;

import com.example.cohortplatform.entities.Grade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    Optional<Grade> findBySubmission_Id(Long submissionId);

    @Query("SELECT g FROM Grade g JOIN FETCH g.gradedBy JOIN FETCH g.submission s JOIN FETCH s.assignment a JOIN FETCH a.course WHERE g.student.id = :studentId")
    List<Grade> findByStudent_Id(Long studentId);

    @Query(value = "SELECT g FROM Grade g JOIN FETCH g.gradedBy JOIN FETCH g.submission s JOIN FETCH s.assignment a JOIN FETCH a.course WHERE g.student.id = :studentId",
            countQuery = "SELECT COUNT(g) FROM Grade g WHERE g.student.id = :studentId")
    Page<Grade> findPageByStudent_Id(Long studentId, Pageable pageable);
}
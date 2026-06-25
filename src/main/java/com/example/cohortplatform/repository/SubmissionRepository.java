package com.example.cohortplatform.repository;

import com.example.cohortplatform.entities.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query("SELECT s FROM Submission s JOIN FETCH s.student JOIN FETCH s.assignment a JOIN FETCH a.course WHERE a.id = :assignmentId")
    List<Submission> findByAssignment_Id(Long assignmentId);

    @Query("SELECT s FROM Submission s JOIN FETCH s.assignment a JOIN FETCH a.course WHERE s.student.id = :studentId")
    List<Submission> findByStudent_Id(Long studentId);

    @Query(value = "SELECT s FROM Submission s JOIN FETCH s.student JOIN FETCH s.assignment a JOIN FETCH a.course WHERE a.id = :assignmentId",
            countQuery = "SELECT COUNT(s) FROM Submission s WHERE s.assignment.id = :assignmentId")
    Page<Submission> findPageByAssignment_Id(Long assignmentId, Pageable pageable);

    @Query(value = "SELECT s FROM Submission s JOIN FETCH s.assignment a JOIN FETCH a.course WHERE s.student.id = :studentId",
            countQuery = "SELECT COUNT(s) FROM Submission s WHERE s.student.id = :studentId")
    Page<Submission> findPageByStudent_Id(Long studentId, Pageable pageable);

    Optional<Submission> findByAssignment_IdAndStudent_Id(Long assignmentId, Long studentId);
}


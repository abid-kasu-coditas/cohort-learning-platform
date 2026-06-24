package com.example.cohortplatform.repository;

import com.example.cohortplatform.entities.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
}
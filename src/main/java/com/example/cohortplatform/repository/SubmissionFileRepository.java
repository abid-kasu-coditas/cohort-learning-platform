package com.example.cohortplatform.repository;

import com.example.cohortplatform.entities.SubmissionFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionFileRepository extends JpaRepository<SubmissionFile, Long> {
}
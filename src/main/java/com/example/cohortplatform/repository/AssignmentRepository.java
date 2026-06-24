package com.example.cohortplatform.repository;

import com.example.cohortplatform.entities.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
}
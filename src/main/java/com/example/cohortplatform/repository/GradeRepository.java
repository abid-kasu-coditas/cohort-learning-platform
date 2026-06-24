package com.example.cohortplatform.repository;

import com.example.cohortplatform.entities.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {
}
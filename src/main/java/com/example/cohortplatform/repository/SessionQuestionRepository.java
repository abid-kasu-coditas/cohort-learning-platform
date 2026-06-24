package com.example.cohortplatform.repository;

import com.example.cohortplatform.entities.SessionQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionQuestionRepository extends JpaRepository<SessionQuestion, Long> {
}
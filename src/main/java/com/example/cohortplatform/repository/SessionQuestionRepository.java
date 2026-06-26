package com.example.cohortplatform.repository;

import com.example.cohortplatform.entities.SessionQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionQuestionRepository extends JpaRepository<SessionQuestion, Long> {
    List<SessionQuestion> findBySession_IdOrderBySentAtAsc(Long sessionId);
}
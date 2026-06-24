package com.example.cohortplatform.repository;

import com.example.cohortplatform.entities.LiveSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LiveSessionRepository extends JpaRepository<LiveSession, Long> {
}
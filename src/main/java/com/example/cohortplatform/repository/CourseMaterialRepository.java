package com.example.cohortplatform.repository;

import com.example.cohortplatform.entities.CourseMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseMaterialRepository extends JpaRepository<CourseMaterial, Long> {
    List<CourseMaterial> findByCourse_Id(Long courseId);

    Page<CourseMaterial> findByCourse_Id(Long courseId, Pageable pageable);
}
package com.example.cohortplatform.repository;

import com.example.cohortplatform.entities.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    @Query("SELECT a FROM Announcement a JOIN FETCH a.postedBy WHERE a.course.id = :courseId ORDER BY a.createdAt DESC")
    List<Announcement> findByCourse_Id(Long courseId);

    @Query(value = "SELECT a FROM Announcement a JOIN FETCH a.postedBy WHERE a.course.id = :courseId",
            countQuery = "SELECT COUNT(a) FROM Announcement a WHERE a.course.id = :courseId")
    Page<Announcement> findPageByCourse_Id(Long courseId, Pageable pageable);
}
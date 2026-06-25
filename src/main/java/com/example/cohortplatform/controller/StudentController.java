package com.example.cohortplatform.controller;

import com.example.cohortplatform.dto.response.*;
import com.example.cohortplatform.security.UserPrincipal;
import com.example.cohortplatform.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentService studentService;


    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<PageResponse<CourseDetailDto>>> getActiveCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("GET /student/courses - page: {}, size: {}", page, size);
        return ResponseEntity.ok(ApiResponse.success(
                studentService.getActiveCourses(PageRequest.of(page, size)), "Courses fetched"));
    }

    @PostMapping("/courses/{courseId}/enroll")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enroll(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserPrincipal principal) {
        log.debug("POST /student/courses/{}/enroll - studentId: {}", courseId, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                studentService.enroll(courseId, principal.getId()), "Enrolled successfully"));
    }


    @GetMapping("/enrollments")
    public ResponseEntity<ApiResponse<PageResponse<EnrollmentResponse>>> getMyEnrollments(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("GET /student/enrollments - studentId: {}, page: {}, size: {}", principal.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(
                studentService.getMyEnrollments(principal.getId(), PageRequest.of(page, size)), "Enrollments fetched"));
    }


    @GetMapping("/courses/{courseId}/materials")
    public ResponseEntity<ApiResponse<PageResponse<MaterialResponse>>> getMaterials(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("GET /student/courses/{}/materials - studentId: {}", courseId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(
                studentService.getMaterials(courseId, principal.getId(), PageRequest.of(page, size)), "Materials fetched"));
    }

    @GetMapping("/courses/{courseId}/announcements")
    public ResponseEntity<ApiResponse<PageResponse<AnnouncementResponse>>> getAnnouncements(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("GET /student/courses/{}/announcements - studentId: {}", courseId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(
                studentService.getAnnouncements(courseId, principal.getId(), PageRequest.of(page, size)), "Announcements fetched"));
    }

    @GetMapping("/courses/{courseId}/assignments")
    public ResponseEntity<ApiResponse<PageResponse<AssignmentResponse>>> getAssignments(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("GET /student/courses/{}/assignments - studentId: {}", courseId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success(
                studentService.getAssignments(courseId, principal.getId(), PageRequest.of(page, size)), "Assignments fetched"));
    }


    @PostMapping(value = "/assignments/{assignmentId}/submit")
    public ResponseEntity<ApiResponse<SubmissionResponse>> submit(
            @PathVariable Long assignmentId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "notes", required = false) String notes,
            @AuthenticationPrincipal UserPrincipal principal) {
        log.debug("POST /student/assignments/{}/submit - studentId: {}, files: {}", assignmentId, principal.getId(), files.size());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                studentService.submit(assignmentId, principal.getId(), files, notes),
                "Submission uploaded successfully"));
    }

    @GetMapping("/submissions")
    public ResponseEntity<ApiResponse<PageResponse<SubmissionResponse>>> getMySubmissions(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("GET /student/submissions - studentId: {}", principal.getId());
        return ResponseEntity.ok(ApiResponse.success(
                studentService.getMySubmissions(principal.getId(), PageRequest.of(page, size)), "Submissions fetched"));
    }

    @GetMapping("/grades")
    public ResponseEntity<ApiResponse<PageResponse<GradeResponse>>> getMyGrades(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("GET /student/grades - studentId: {}", principal.getId());
        return ResponseEntity.ok(ApiResponse.success(
                studentService.getMyGrades(principal.getId(), PageRequest.of(page, size)), "Grades fetched"));
    }

}

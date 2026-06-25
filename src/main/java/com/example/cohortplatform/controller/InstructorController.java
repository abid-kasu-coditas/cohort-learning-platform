package com.example.cohortplatform.controller;


import com.example.cohortplatform.dto.request.CreateAnnouncementRequest;
import com.example.cohortplatform.dto.request.CreateAssignmentRequest;
import com.example.cohortplatform.dto.request.GradeSubmissionRequest;
import com.example.cohortplatform.dto.response.*;
import com.example.cohortplatform.security.UserPrincipal;
import com.example.cohortplatform.service.InstructorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/instructor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('INSTRUCTOR')")
@Slf4j
public class InstructorController {

    private final InstructorService instructorService;


    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<PageResponse<CourseDetailDto>>> getMyCourses(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                instructorService.getMyCourses(principal.getId(), PageRequest.of(page, size)), "Courses fetched"));
    }

    @GetMapping("/courses/{courseId}/students")
    public ResponseEntity<ApiResponse<PageResponse<UserSummaryDto>>> getCourseStudents(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                instructorService.getCourseStudents(courseId, principal.getId(), PageRequest.of(page, size)), "Students fetched"));
    }


    @PostMapping(value = "/courses/{courseId}/materials")
    public ResponseEntity<ApiResponse<MaterialResponse>> uploadMaterial(
            @PathVariable Long courseId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                instructorService.uploadMaterial(courseId, principal.getId(), file), "Material uploaded"));
    }

    @DeleteMapping("/courses/{courseId}/materials/{materialId}")
    public ResponseEntity<ApiResponse<Void>> deleteMaterial(
            @PathVariable Long courseId,
            @PathVariable Long materialId,
            @AuthenticationPrincipal UserPrincipal principal) {
        instructorService.deleteMaterial(courseId, materialId, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Material deleted"));
    }


    @PostMapping("/courses/{courseId}/announcements")
    public ResponseEntity<ApiResponse<AnnouncementResponse>> createAnnouncement(
            @PathVariable Long courseId,
            @Valid @RequestBody CreateAnnouncementRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                instructorService.createAnnouncement(courseId, principal.getId(), request), "Announcement posted"));
    }

    @GetMapping("/courses/{courseId}/announcements")
    public ResponseEntity<ApiResponse<PageResponse<AnnouncementResponse>>> getAnnouncements(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                instructorService.getAnnouncements(courseId, principal.getId(), PageRequest.of(page, size)), "Announcements fetched"));
    }


    @PostMapping("/courses/{courseId}/assignments")
    public ResponseEntity<ApiResponse<AssignmentResponse>> createAssignment(
            @PathVariable Long courseId,
            @Valid @RequestBody CreateAssignmentRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                instructorService.createAssignment(courseId, principal.getId(), request), "Assignment created"));
    }

    @GetMapping("/courses/{courseId}/assignments")
    public ResponseEntity<ApiResponse<PageResponse<AssignmentResponse>>> getAssignments(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                instructorService.getAssignments(courseId, principal.getId(), PageRequest.of(page, size)), "Assignments fetched"));
    }

    @GetMapping("/courses/{courseId}/assignments/{assignmentId}/submissions")
    public ResponseEntity<ApiResponse<PageResponse<SubmissionDetailResponse>>> getSubmissions(
            @PathVariable Long courseId,
            @PathVariable Long assignmentId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                instructorService.getSubmissions(courseId, assignmentId, principal.getId(), PageRequest.of(page, size)), "Submissions fetched"));
    }


    @PostMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<ApiResponse<SubmissionDetailResponse>> gradeSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody GradeSubmissionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                instructorService.gradeSubmission(submissionId, principal.getId(), request), "Submission graded"));
    }

}

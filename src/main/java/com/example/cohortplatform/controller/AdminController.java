package com.example.cohortplatform.controller;

import com.example.cohortplatform.dto.request.AssignInstructorRequest;
import com.example.cohortplatform.dto.request.CreateCourseRequest;
import com.example.cohortplatform.dto.request.UpdateCourseRequest;
import com.example.cohortplatform.dto.request.UpdateEnrollmentStatusRequest;
import com.example.cohortplatform.dto.response.*;
import com.example.cohortplatform.entities.enums.UserRole;
import com.example.cohortplatform.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;


    @PostMapping("/courses")
    public ResponseEntity<ApiResponse<CourseDetailDto>> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        log.debug("POST /courses - title: {}", request.title());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(adminService.createCourse(request), "Course created"));
    }

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<PageResponse<CourseDetailDto>>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("GET /courses - page: {}, size: {}", page, size);
        return ResponseEntity.ok(ApiResponse.success(
                adminService.getAllCourses(PageRequest.of(page, size, Sort.by("id").descending())),
                "Courses fetched"));
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<CourseDetailDto>> getCourse(@PathVariable Long id) {
        log.debug("GET /courses/{}", id);
        return ResponseEntity.ok(ApiResponse.success(adminService.getCourse(id), "Course fetched"));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<CourseDetailDto>> updateCourse(
            @PathVariable Long id, @Valid @RequestBody UpdateCourseRequest request) {
        log.debug("PUT /courses/{}", id);
        return ResponseEntity.ok(ApiResponse.success(adminService.updateCourse(id, request), "Course updated"));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        log.debug("DELETE /courses/{}", id);
        adminService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success("Course deleted"));
    }

    @PutMapping("/courses/{id}/assign-instructor")
    public ResponseEntity<ApiResponse<CourseDetailDto>> assignInstructor(
            @PathVariable Long id, @Valid @RequestBody AssignInstructorRequest request) {
        log.debug("PUT /courses/{}/assign-instructor - instructorId: {}", id, request.instructorId());
        return ResponseEntity.ok(ApiResponse.success(adminService.assignInstructor(id, request), "Instructor assigned"));
    }


    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PageResponse<AdminUserResponse>>> getUsers(
            @RequestParam(required = false) UserRole role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("GET /users - role: {}, page: {}, size: {}", role, page, size);
        return ResponseEntity.ok(ApiResponse.success(
                adminService.getUsers(role, PageRequest.of(page, size, Sort.by("id").descending())),
                "Users fetched"));
    }


    @GetMapping("/enrollments")
    public ResponseEntity<ApiResponse<PageResponse<EnrollmentResponse>>> getAllEnrollments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("GET /enrollments - page: {}, size: {}", page, size);
        return ResponseEntity.ok(ApiResponse.success(
                adminService.getAllEnrollments(PageRequest.of(page, size, Sort.by("id").descending())),
                "Enrollments fetched"));
    }

    @GetMapping("/enrollments/course/{courseId}")
    public ResponseEntity<ApiResponse<PageResponse<EnrollmentResponse>>> getEnrollmentsByCourse(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("GET /enrollments/course/{} - page: {}, size: {}", courseId, page, size);
        return ResponseEntity.ok(ApiResponse.success(
                adminService.getEnrollmentsByCourse(courseId, PageRequest.of(page, size, Sort.by("id").descending())),
                "Enrollments fetched"));
    }

    @GetMapping("/enrollments/student/{studentId}")
    public ResponseEntity<ApiResponse<PageResponse<EnrollmentResponse>>> getEnrollmentsByStudent(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("GET /enrollments/student/{} - page: {}, size: {}", studentId, page, size);
        return ResponseEntity.ok(ApiResponse.success(
                adminService.getEnrollmentsByStudent(studentId, PageRequest.of(page, size, Sort.by("id").descending())),
                "Enrollments fetched"));
    }

    @PutMapping("/enrollments/{enrollmentId}/status")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> updateEnrollmentStatus(
            @PathVariable Long enrollmentId, @Valid @RequestBody UpdateEnrollmentStatusRequest request) {
        log.debug("PUT /enrollments/{}/status - status: {}", enrollmentId, request.status());
        return ResponseEntity.ok(ApiResponse.success(adminService.updateEnrollmentStatus(enrollmentId, request), "Status updated"));
    }

    @DeleteMapping("/enrollments/{enrollmentId}")
    public ResponseEntity<ApiResponse<Void>> deleteEnrollment(@PathVariable Long enrollmentId) {
        log.debug("DELETE /enrollments/{}", enrollmentId);
        adminService.deleteEnrollment(enrollmentId);
        return ResponseEntity.ok(ApiResponse.success("Enrollment removed"));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        log.debug("GET /dashboard");
        return ResponseEntity.ok(ApiResponse.success(adminService.getDashboard(), "Dashboard fetched"));
    }
}


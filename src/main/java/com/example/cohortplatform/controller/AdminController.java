package com.example.cohortplatform.controller;

import com.example.cohortplatform.dto.request.*;
import com.example.cohortplatform.dto.response.*;
import com.example.cohortplatform.entities.enums.UserRole;
import com.example.cohortplatform.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getDashboard(), "Dashboard retrieved"));
    }

    @PostMapping("/courses")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(adminService.createCourse(request), "Course created successfully"));
    }

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<PageResponse<CourseResponse>>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllCourses(pageable), "Courses retrieved"));
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getCourseById(id), "Course retrieved"));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCourseRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminService.updateCourse(id, request), "Course updated successfully"));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateCourse(@PathVariable Long id) {
        adminService.deactivateCourse(id);
        return ResponseEntity.ok(ApiResponse.success("Course deactivated successfully"));
    }

    @PutMapping("/courses/{id}/instructor")
    public ResponseEntity<ApiResponse<CourseResponse>> assignInstructor(
            @PathVariable Long id,
            @Valid @RequestBody AssignInstructorRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminService.assignInstructor(id, request), "Instructor assigned successfully"));
    }

    @GetMapping("/courses/{id}/enrollments")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getCourseEnrollments(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getCourseEnrollments(id), "Enrollments retrieved"));
    }


    @GetMapping("/users")
    public ResponseEntity<ApiResponse<PageResponse<UserSummaryResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllUsers(pageable), "Users retrieved"));
    }

    @GetMapping("/users/students")
    public ResponseEntity<ApiResponse<PageResponse<UserSummaryResponse>>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(adminService.getUsersByRole(UserRole.STUDENT, pageable), "Students retrieved"));
    }

    @GetMapping("/users/instructors")
    public ResponseEntity<ApiResponse<PageResponse<UserSummaryResponse>>> getAllInstructors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(adminService.getUsersByRole(UserRole.INSTRUCTOR, pageable), "Instructors retrieved"));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getUserById(id), "User retrieved"));
    }

    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable Long id) {
        adminService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully"));
    }

    @PutMapping("/users/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable Long id) {
        adminService.activateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User activated successfully"));
    }

    @GetMapping("/users/{id}/enrollments")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getStudentEnrollments(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getStudentEnrollments(id), "Enrollments retrieved"));
    }


    @PostMapping("/enrollments")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enrollStudent(@Valid @RequestBody EnrollStudentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(adminService.enrollStudent(request), "Student enrolled successfully"));
    }

    @GetMapping("/enrollments")
    public ResponseEntity<ApiResponse<PageResponse<EnrollmentResponse>>> getAllEnrollments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("enrolledAt").descending());
        return ResponseEntity.ok(ApiResponse.success(adminService.getAllEnrollments(pageable), "Enrollments retrieved"));
    }

    @PutMapping("/enrollments/{id}/status")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> updateEnrollmentStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEnrollmentStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminService.updateEnrollmentStatus(id, request), "Enrollment status updated"));
    }

    @DeleteMapping("/enrollments/{id}")
    public ResponseEntity<ApiResponse<Void>> removeEnrollment(@PathVariable Long id) {
        adminService.removeEnrollment(id);
        return ResponseEntity.ok(ApiResponse.success("Enrollment removed successfully"));
    }
}

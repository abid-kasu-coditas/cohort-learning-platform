package com.example.cohortplatform.service;

import com.example.cohortplatform.dto.request.*;
import com.example.cohortplatform.dto.response.*;
import com.example.cohortplatform.entities.Course;
import com.example.cohortplatform.entities.CourseEnrollment;
import com.example.cohortplatform.entities.User;
import com.example.cohortplatform.entities.enums.EnrollmentStatus;
import com.example.cohortplatform.entities.enums.UserRole;
import com.example.cohortplatform.exception.CourseCapacityExceededException;
import com.example.cohortplatform.exception.DuplicateEnrollmentException;
import com.example.cohortplatform.exception.ResourceNotFoundException;
import com.example.cohortplatform.exception.UnauthorizedException;
import com.example.cohortplatform.repository.CourseEnrollmentRepository;
import com.example.cohortplatform.repository.CourseRepository;
import com.example.cohortplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;


    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request) {
        User instructor = findInstructor(request.instructorId());
        Course course = Course.builder()
                .title(request.title())
                .description(request.description())
                .maxCapacity(request.maxCapacity())
                .instructor(instructor)
                .createdAt(LocalDateTime.now())
                .build();
        courseRepository.save(course);
        return toCourseResponse(course, 0L);
    }

    public PageResponse<CourseResponse> getAllCourses(Pageable pageable) {
        Page<CourseResponse> page = courseRepository.findAll(pageable)
                .map(c -> toCourseResponse(c, enrollmentRepository.countByCourse_Id(c.getId())));
        return PageResponse.of(page);
    }

    public CourseResponse getCourseById(Long id) {
        Course course = findCourse(id);
        long count = enrollmentRepository.countByCourse_Id(id);
        return toCourseResponse(course, count);
    }

    @Transactional
    public CourseResponse updateCourse(Long id, UpdateCourseRequest request) {
        Course course = findCourse(id);
        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setMaxCapacity(request.maxCapacity());
        course.setUpdatedAt(LocalDateTime.now());
        courseRepository.save(course);
        long count = enrollmentRepository.countByCourse_Id(id);
        return toCourseResponse(course, count);
    }

    @Transactional
    public void deactivateCourse(Long id) {
        Course course = findCourse(id);
        course.setActive(false);
        course.setUpdatedAt(LocalDateTime.now());
        courseRepository.save(course);
    }

    @Transactional
    public CourseResponse assignInstructor(Long courseId, AssignInstructorRequest request) {
        Course course = findCourse(courseId);
        User instructor = findInstructor(request.instructorId());
        course.setInstructor(instructor);
        course.setUpdatedAt(LocalDateTime.now());
        courseRepository.save(course);
        long count = enrollmentRepository.countByCourse_Id(courseId);
        return toCourseResponse(course, count);
    }


    public PageResponse<UserSummaryResponse> getAllUsers(Pageable pageable) {
        Page<UserSummaryResponse> page = userRepository.findAll(pageable)
                .map(this::toUserSummary);
        return PageResponse.of(page);
    }

    public PageResponse<UserSummaryResponse> getUsersByRole(UserRole role, Pageable pageable) {
        Page<UserSummaryResponse> page = userRepository.findAllByRole(role, pageable)
                .map(this::toUserSummary);
        return PageResponse.of(page);
    }

    public UserSummaryResponse getUserById(Long id) {
        return toUserSummary(findUser(id));
    }

    @Transactional
    public void deactivateUser(Long id) {
        User user = findUser(id);
        if (user.getRole() == UserRole.SUPER_ADMIN) {
            throw new UnauthorizedException("Cannot deactivate a super admin account");
        }
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void activateUser(Long id) {
        User user = findUser(id);
        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }


    @Transactional
    public EnrollmentResponse enrollStudent(EnrollStudentRequest request) {
        User student = findUser(request.studentId());
        if (student.getRole() != UserRole.STUDENT) {
            throw new UnauthorizedException("User is not a student");
        }

        Course course = findCourse(request.courseId());
        if (!course.isActive()) {
            throw new UnauthorizedException("Cannot enroll in an inactive course");
        }

        if (enrollmentRepository.existsByCourse_IdAndUser_Id(course.getId(), student.getId())) {
            throw new DuplicateEnrollmentException("Student is already enrolled in this course");
        }

        long currentEnrollments = enrollmentRepository.countByCourse_Id(course.getId());
        if (course.getMaxCapacity() != null && currentEnrollments >= course.getMaxCapacity()) {
            throw new CourseCapacityExceededException(
                    "Course is full — maximum capacity of " + course.getMaxCapacity() + " has been reached");
        }

        CourseEnrollment enrollment = CourseEnrollment.builder()
                .course(course)
                .user(student)
                .status(EnrollmentStatus.ONGOING)
                .enrolledAt(LocalDateTime.now())
                .build();
        enrollmentRepository.save(enrollment);
        return toEnrollmentResponse(enrollment);
    }

    public PageResponse<EnrollmentResponse> getAllEnrollments(Pageable pageable) {
        Page<EnrollmentResponse> page = enrollmentRepository.findAll(pageable)
                .map(this::toEnrollmentResponse);
        return PageResponse.of(page);
    }

    public List<EnrollmentResponse> getCourseEnrollments(Long courseId) {
        findCourse(courseId);
        return enrollmentRepository.findAllByCourse_Id(courseId).stream()
                .map(this::toEnrollmentResponse).toList();
    }

    public List<EnrollmentResponse> getStudentEnrollments(Long studentId) {
        findUser(studentId);
        return enrollmentRepository.findAllByUser_Id(studentId).stream()
                .map(this::toEnrollmentResponse).toList();
    }

    @Transactional
    public EnrollmentResponse updateEnrollmentStatus(Long enrollmentId, UpdateEnrollmentStatusRequest request) {
        CourseEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId));
        enrollment.setStatus(request.status());
        enrollmentRepository.save(enrollment);
        return toEnrollmentResponse(enrollment);
    }

    @Transactional
    public void removeEnrollment(Long enrollmentId) {
        if (!enrollmentRepository.existsById(enrollmentId)) {
            throw new ResourceNotFoundException("Enrollment not found with id: " + enrollmentId);
        }
        enrollmentRepository.deleteById(enrollmentId);
    }


    public AdminDashboardResponse getDashboard() {
        return AdminDashboardResponse.builder()
                .totalCourses(courseRepository.count())
                .totalActiveCourses(courseRepository.countByIsActive(true))
                .totalStudents(userRepository.countByRole(UserRole.STUDENT))
                .totalInstructors(userRepository.countByRole(UserRole.INSTRUCTOR))
                .totalEnrollments(enrollmentRepository.count())
                .build();
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private Course findCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private User findInstructor(Long id) {
        User user = findUser(id);
        if (user.getRole() != UserRole.INSTRUCTOR) {
            throw new UnauthorizedException("User with id " + id + " is not an instructor");
        }
        return user;
    }

    private CourseResponse toCourseResponse(Course course, long enrollmentCount) {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .maxCapacity(course.getMaxCapacity())
                .isActive(course.isActive())
                .instructorId(course.getInstructor().getId())
                .instructorName(course.getInstructor().getFirstname() + " " + course.getInstructor().getLastname())
                .enrollmentCount(enrollmentCount)
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    private UserSummaryResponse toUserSummary(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private EnrollmentResponse toEnrollmentResponse(CourseEnrollment enrollment) {
        User student = enrollment.getUser();
        Course course = enrollment.getCourse();
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .studentId(student.getId())
                .studentName(student.getFirstname() + " " + student.getLastname())
                .studentEmail(student.getEmail())
                .status(enrollment.getStatus())
                .enrolledAt(enrollment.getEnrolledAt())
                .build();
    }
}

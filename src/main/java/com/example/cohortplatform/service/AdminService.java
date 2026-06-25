package com.example.cohortplatform.service;

import com.example.cohortplatform.dto.request.AssignInstructorRequest;
import com.example.cohortplatform.dto.request.CreateCourseRequest;
import com.example.cohortplatform.dto.request.UpdateCourseRequest;
import com.example.cohortplatform.dto.request.UpdateEnrollmentStatusRequest;
import com.example.cohortplatform.dto.response.*;
import com.example.cohortplatform.entities.Course;
import com.example.cohortplatform.entities.CourseEnrollment;
import com.example.cohortplatform.entities.User;
import com.example.cohortplatform.entities.enums.UserRole;
import com.example.cohortplatform.exception.ResourceNotFoundException;
import com.example.cohortplatform.exception.UnauthorizedException;
import com.example.cohortplatform.mapper.CourseMapper;
import com.example.cohortplatform.mapper.EnrollmentMapper;
import com.example.cohortplatform.mapper.UserMapper;
import com.example.cohortplatform.repository.CourseEnrollmentRepository;
import com.example.cohortplatform.repository.CourseRepository;
import com.example.cohortplatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final EmailService emailService;
    private final CourseMapper courseMapper;
    private final UserMapper userMapper;
    private final EnrollmentMapper enrollmentMapper;


    @Transactional
    public CourseDetailDto createCourse(CreateCourseRequest request) {
        log.info("Creating course: title={}, instructorId={}", request.title(), request.instructorId());
        User instructor = (request.instructorId() != null) ? getInstructor(request.instructorId()) : null;
        Course course = Course.builder()
                .title(request.title())
                .description(request.description())
                .maxCapacity(request.maxCapacity())
                .instructor(instructor)
                .build();
        CourseDetailDto result = courseMapper.toDetail(courseRepository.save(course));
        log.info("Course created: id={}, title={}", result.id(), result.title());
        return result;
    }

    public PageResponse<CourseDetailDto> getAllCourses(Pageable pageable) {
        log.debug("Fetching all courses - page: {}", pageable.getPageNumber());
        Page<CourseDetailDto> page = courseRepository.findAllWithInstructor(pageable)
                .map(courseMapper::toDetail);
        return PageResponse.of(page);
    }

    public CourseDetailDto getCourse(Long id) {
        log.debug("Fetching courseId={}", id);
        return courseMapper.toDetail(findCourseWithInstructor(id));
    }

    @Transactional
    public CourseDetailDto updateCourse(Long id, UpdateCourseRequest request) {
        log.info("Updating courseId={}", id);
        Course course = findCourseWithInstructor(id);
        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setMaxCapacity(request.maxCapacity());
        return courseMapper.toDetail(courseRepository.save(course));
    }

    @Transactional
    public void deleteCourse(Long id) {
        log.info("Deleting courseId={}", id);
        if (!courseRepository.existsById(id)) throw new ResourceNotFoundException("Course not found: " + id);
        courseRepository.deleteById(id);
    }

    @Transactional
    public CourseDetailDto assignInstructor(Long courseId, AssignInstructorRequest request) {
        log.info("Assigning instructorId={} to courseId={}", request.instructorId(), courseId);
        Course course = findCourseWithInstructor(courseId);
        User instructor = getInstructor(request.instructorId());
        course.setInstructor(instructor);
        Course saved = courseRepository.save(course);

        enrollmentRepository.findAllByCourse_Id(courseId).forEach(e ->
                emailService.sendCourseWelcomeMail(
                        e.getUser().getEmail(),
                        e.getUser().getFirstname(),
                        saved.getTitle()
                ));

        emailService.sendCourseAssignmentMail(instructor.getEmail(), instructor.getFirstname(), course.getTitle());

        return courseMapper.toDetail(saved);
    }

    public PageResponse<AdminUserResponse> getUsers(UserRole role, Pageable pageable) {
        log.debug("Fetching users - role={}, page={}", role, pageable.getPageNumber());
        Page<User> page = (role != null)
                ? userRepository.findAllByRole(role, pageable)
                : userRepository.findAll(pageable);
        return PageResponse.of(page.map(userMapper::toAdminResponse));
    }


    public PageResponse<EnrollmentResponse> getAllEnrollments(Pageable pageable) {
        log.debug("Fetching all enrollments - page={}", pageable.getPageNumber());
        Page<EnrollmentResponse> page = enrollmentRepository.findAllWithDetails(pageable)
                .map(enrollmentMapper::toResponse);
        return PageResponse.of(page);
    }

    public PageResponse<EnrollmentResponse> getEnrollmentsByCourse(Long courseId, Pageable pageable) {
        log.debug("Fetching enrollments for courseId={}", courseId);
        if (!courseRepository.existsById(courseId))
            throw new ResourceNotFoundException("Course not found: " + courseId);
        Page<EnrollmentResponse> page = enrollmentRepository.findPageByCourse_Id(courseId, pageable)
                .map(enrollmentMapper::toResponse);
        return PageResponse.of(page);
    }

    public PageResponse<EnrollmentResponse> getEnrollmentsByStudent(Long studentId, Pageable pageable) {
        log.debug("Fetching enrollments for studentId={}", studentId);
        userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));
        Page<EnrollmentResponse> page = enrollmentRepository.findPageByUser_Id(studentId, pageable)
                .map(enrollmentMapper::toResponse);
        return PageResponse.of(page);
    }

    @Transactional
    public EnrollmentResponse updateEnrollmentStatus(Long enrollmentId, UpdateEnrollmentStatusRequest request) {
        log.info("Updating enrollmentId={} status to {}", enrollmentId, request.status());
        CourseEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found: " + enrollmentId));
        enrollment.setStatus(request.status());
        return enrollmentMapper.toResponse(enrollmentRepository.save(enrollment));
    }

    @Transactional
    public void deleteEnrollment(Long enrollmentId) {
        log.info("Deleting enrollmentId={}", enrollmentId);
        if (!enrollmentRepository.existsById(enrollmentId))
            throw new ResourceNotFoundException("Enrollment not found: " + enrollmentId);
        enrollmentRepository.deleteById(enrollmentId);
    }


    public DashboardResponse getDashboard() {
        return DashboardResponse.builder()
                .totalCourses(courseRepository.count())
                .activeCourses(courseRepository.countByIsActive(true))
                .totalStudents(userRepository.countByRole(UserRole.STUDENT))
                .totalInstructors(userRepository.countByRole(UserRole.INSTRUCTOR))
                .totalEnrollments(enrollmentRepository.count())
                .build();
    }


    private Course findCourseWithInstructor(Long id) {
        return courseRepository.findByIdWithInstructor(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + id));
    }

    private User getInstructor(Long instructorId) {
        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + instructorId));
        if (user.getRole() != UserRole.INSTRUCTOR)
            throw new UnauthorizedException("User " + instructorId + " is not an INSTRUCTOR");
        return user;
    }

}

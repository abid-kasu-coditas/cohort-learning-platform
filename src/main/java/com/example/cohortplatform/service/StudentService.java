package com.example.cohortplatform.service;


import com.example.cohortplatform.dto.response.*;
import com.example.cohortplatform.entities.*;
import com.example.cohortplatform.entities.enums.EnrollmentStatus;
import com.example.cohortplatform.entities.enums.SubmissionStatus;
import com.example.cohortplatform.exception.*;
import com.example.cohortplatform.mapper.*;
import com.example.cohortplatform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {


    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final CourseMaterialRepository materialRepository;
    private final AnnouncementRepository announcementRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final SubmissionFileRepository submissionFileRepository;
    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;

    private final CourseMapper courseMapper;
    private final UserMapper userMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final MaterialMapper materialMapper;
    private final AnnouncementMapper announcementMapper;
    private final AssignmentMapper assignmentMapper;
    private final SubmissionMapper submissionMapper;
    private final GradeMapper gradeMapper;

    private final EmailService emailService;
    private final FileStorageService fileStorageService;


    public PageResponse<CourseDetailDto> getActiveCourses(Pageable pageable) {
        log.debug("Fetching active courses - page: {}", pageable.getPageNumber());
        return PageResponse.of(courseRepository.findAllActive(pageable).map(courseMapper::toDetail));
    }

    @Transactional
    public EnrollmentResponse enroll(Long courseId, Long studentId) {
        log.info("Student {} enrolling in course {}", studentId, courseId);
        Course course = courseRepository.findByIdWithInstructor(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

        if (!course.isActive())
            throw new ResourceNotFoundException("Course is not active");

        if (enrollmentRepository.existsByCourse_IdAndUser_Id(courseId, studentId))
            throw new DuplicateEnrollmentException("You are already enrolled in this course");

        long enrolled = enrollmentRepository.countByCourse_Id(courseId);
        if (course.getMaxCapacity() != null && enrolled >= course.getMaxCapacity())
            throw new CourseCapacityExceededException("Course has reached maximum capacity");

        User student = userRepository.getReferenceById(studentId);
        CourseEnrollment enrollment = CourseEnrollment.builder()
                .course(course)
                .user(student)
                .status(EnrollmentStatus.ONGOING)
                .build();
        CourseEnrollment saved = enrollmentRepository.save(enrollment);

        User studentFull = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));
        emailService.sendCourseWelcomeMail(studentFull.getEmail(), studentFull.getFirstname(), course.getTitle());
        log.info("Student {} enrolled in course {} successfully", studentId, courseId);

        return enrollmentMapper.toResponse(saved);
    }

    public PageResponse<EnrollmentResponse> getMyEnrollments(Long studentId, Pageable pageable) {
        log.debug("Fetching enrollments for studentId={}", studentId);
        return PageResponse.of(enrollmentRepository.findPageByUser_Id(studentId, pageable).map(enrollmentMapper::toResponse));
    }

    public PageResponse<MaterialResponse> getMaterials(Long courseId, Long studentId, Pageable pageable) {
        log.debug("Fetching materials for courseId={}, studentId={}", courseId, studentId);
        verifyEnrollment(courseId, studentId);
        return PageResponse.of(materialRepository.findByCourse_Id(courseId, pageable).map(materialMapper::toResponse));
    }

    public PageResponse<AnnouncementResponse> getAnnouncements(Long courseId, Long studentId, Pageable pageable) {
        log.debug("Fetching announcements for courseId={}, studentId={}", courseId, studentId);
        verifyEnrollment(courseId, studentId);
        return PageResponse.of(announcementRepository.findPageByCourse_Id(courseId, pageable).map(announcementMapper::toResponse));
    }

    public PageResponse<AssignmentResponse> getAssignments(Long courseId, Long studentId, Pageable pageable) {
        log.debug("Fetching assignments for courseId={}, studentId={}", courseId, studentId);
        verifyEnrollment(courseId, studentId);
        return PageResponse.of(assignmentRepository.findPageByCourse_Id(courseId, pageable).map(assignmentMapper::toResponse));
    }

    @Transactional
    public SubmissionResponse submit(Long assignmentId, Long studentId,
                                     List<MultipartFile> files, String notes) {
        log.info("Student {} submitting assignment {} with {} file(s)", studentId, assignmentId, files.size());
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + assignmentId));

        verifyEnrollment(assignment.getCourse().getId(), studentId);

        if (submissionRepository.findByAssignment_IdAndStudent_Id(assignmentId, studentId).isPresent())
            throw new DuplicateSubmissionException("You have already submitted this assignment");

        if (LocalDateTime.now().isAfter(assignment.getDeadline()))
            throw new DeadlinePassedException("Submission deadline has passed");

        User student = userRepository.getReferenceById(studentId);
        Submission submission = Submission.builder()
                .assignment(assignment)
                .student(student)
                .notes(notes)
                .status(SubmissionStatus.SUBMITTED)
                .build();
        Submission saved = submissionRepository.save(submission);

        for (MultipartFile file : files) {
            String key = fileStorageService.upload(file,
                    "submissions/" + assignmentId + "/" + studentId);
            SubmissionFile sf = SubmissionFile.builder()
                    .fileName(file.getOriginalFilename())
                    .filePath(key)
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .submission(saved)
                    .build();
            submissionFileRepository.save(sf);
        }

        log.info("Submission saved for studentId={}, assignmentId={}", studentId, assignmentId);
        return SubmissionResponse.builder()
                .submissionId(submission.getId())
                .status(submission.getStatus())
                .submittedAt(LocalDateTime.now())
                .build();

    }

    public PageResponse<SubmissionResponse> getMySubmissions(Long studentId, Pageable pageable) {
        log.debug("Fetching submissions for studentId={}", studentId);
        return PageResponse.of(submissionRepository.findPageByStudent_Id(studentId, pageable)
                .map(s -> submissionMapper.toStudentView(s, gradeRepository.findBySubmission_Id(s.getId()))));
    }

    public PageResponse<GradeResponse> getMyGrades(Long studentId, Pageable pageable) {
        log.debug("Fetching grades for studentId={}", studentId);
        return PageResponse.of(gradeRepository.findPageByStudent_Id(studentId, pageable).map(gradeMapper::toResponse));
    }

    private void verifyEnrollment(Long courseId, Long studentId) {
        if (!enrollmentRepository.existsByCourse_IdAndUser_Id(courseId, studentId))
            throw new UnauthorizedException("You are not enrolled in this course");
    }
}

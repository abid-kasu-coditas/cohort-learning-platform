package com.example.cohortplatform.service;

import com.example.cohortplatform.dto.request.CreateAnnouncementRequest;
import com.example.cohortplatform.dto.request.CreateAssignmentRequest;
import com.example.cohortplatform.dto.request.FeedbackDetail;
import com.example.cohortplatform.dto.request.GradeSubmissionRequest;
import com.example.cohortplatform.dto.response.*;
import com.example.cohortplatform.entities.*;
import com.example.cohortplatform.entities.enums.ContentType;
import com.example.cohortplatform.entities.enums.SubmissionStatus;
import com.example.cohortplatform.exception.ResourceNotFoundException;
import com.example.cohortplatform.exception.UnauthorizedException;
import com.example.cohortplatform.mapper.*;
import com.example.cohortplatform.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstructorService {

    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final CourseMaterialRepository materialRepository;
    private final AnnouncementRepository announcementRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final GradeRepository gradeRepository;
    private final LiveSessionRepository sessionRepository;
    private final SessionQuestionRepository questionRepository;
    private final UserRepository userRepository;

    private final FileStorageService fileStorageService;
    private final EmailService emailService;

    private final CourseMapper courseMapper;
    private final UserMapper userMapper;
    private final EnrollmentMapper enrollmentMapper;
    private final MaterialMapper materialMapper;
    private final AnnouncementMapper announcementMapper;
    private final AssignmentMapper assignmentMapper;
    private final SubmissionMapper submissionMapper;
    private final SessionMapper sessionMapper;


    public PageResponse<CourseDetailDto> getMyCourses(Long instructorId, Pageable pageable) {
        log.debug("Fetching courses for instructorId={}", instructorId);
        return PageResponse.of(courseRepository.findPageByInstructor_Id(instructorId, pageable).map(courseMapper::toDetail));
    }

    public PageResponse<UserSummaryDto> getCourseStudents(Long courseId, Long instructorId, Pageable pageable) {
        log.debug("Fetching students for courseId={}, instructorId={}", courseId, instructorId);
        verifyOwnership(courseId, instructorId);
        return PageResponse.of(enrollmentRepository.findPageByCourse_Id(courseId, pageable).map(e -> userMapper.toSummary(e.getUser())));
    }


    @Transactional
    public MaterialResponse uploadMaterial(Long courseId, Long instructorId, MultipartFile file) {
        log.info("Instructor {} uploading material '{}' to courseId={}", instructorId, file.getOriginalFilename(), courseId);
        Course course = verifyOwnership(courseId, instructorId);
        String key = fileStorageService.upload(file, "materials/" + courseId);
        ContentType contentType = resolveContentType(file.getContentType());
        CourseMaterial material = CourseMaterial.builder()
                .filename(file.getOriginalFilename())
                .filePath(key)
                .contentType(contentType)
                .course(course)
                .build();
        CourseMaterial saved = materialRepository.save(material);

         enrollmentRepository.findAllByCourse_Id(courseId).forEach(e ->
                emailService.sendAnnouncementMail(
                        e.getUser().getEmail(),
                        e.getUser().getFirstname(),
                        "New material: " + file.getOriginalFilename(),
                        course.getTitle()
                ));

        return materialMapper.toResponse(saved);
    }

    @Transactional
    public void deleteMaterial(Long courseId, Long materialId, Long instructorId) {
        log.info("Instructor {} deleting materialId={} from courseId={}", instructorId, materialId, courseId);
        verifyOwnership(courseId, instructorId);
        CourseMaterial material = materialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found: " + materialId));
        if (!material.getCourse().getId().equals(courseId))
            throw new UnauthorizedException("Material does not belong to this course");
        fileStorageService.delete(material.getFilePath());
        materialRepository.delete(material);
    }


    @Transactional
    public AnnouncementResponse createAnnouncement(Long courseId, Long instructorId, CreateAnnouncementRequest request) {
        log.info("Instructor {} creating announcement in courseId={}", instructorId, courseId);
        Course course = verifyOwnership(courseId, instructorId);
        User instructor = userRepository.getReferenceById(instructorId);
        Announcement announcement = Announcement.builder()
                .title(request.title())
                .content(request.content())
                .course(course)
                .postedBy(instructor)
                .build();
        Announcement saved = announcementRepository.save(announcement);

        enrollmentRepository.findAllByCourse_Id(courseId).forEach(e ->
                emailService.sendAnnouncementMail(
                        e.getUser().getEmail(),
                        e.getUser().getFirstname(),
                        request.title(),
                        course.getTitle()
                ));

        return announcementMapper.toResponse(saved);
    }

    public PageResponse<AnnouncementResponse> getAnnouncements(Long courseId, Long instructorId, Pageable pageable) {
        log.debug("Fetching announcements for courseId={}, instructorId={}", courseId, instructorId);
        verifyOwnership(courseId, instructorId);
        return PageResponse.of(announcementRepository.findPageByCourse_Id(courseId, pageable).map(announcementMapper::toResponse));
    }


    @Transactional
    public AssignmentResponse createAssignment(Long courseId, Long instructorId, CreateAssignmentRequest request) {
        log.info("Instructor {} creating assignment in courseId={}", instructorId, courseId);
        Course course = verifyOwnership(courseId, instructorId);
        User instructor = userRepository.getReferenceById(instructorId);
        Assignment assignment = Assignment.builder()
                .title(request.title())
                .description(request.description())
                .deadline(request.deadline())
                .course(course)
                .instructor(instructor)
                .build();
        Assignment saved = assignmentRepository.save(assignment);

        enrollmentRepository.findAllByCourse_Id(courseId).forEach(e ->
                emailService.sendAssignmentNotificationMail(
                        e.getUser().getEmail(),
                        e.getUser().getFirstname(),
                        request.title(),
                        course.getTitle(),
                        request.deadline().toString()
                ));

        return assignmentMapper.toResponse(saved);
    }

    public PageResponse<AssignmentResponse> getAssignments(Long courseId, Long instructorId, Pageable pageable) {
        log.debug("Fetching assignments for courseId={}, instructorId={}", courseId, instructorId);
        verifyOwnership(courseId, instructorId);
        return PageResponse.of(assignmentRepository.findPageByCourse_Id(courseId, pageable).map(assignmentMapper::toResponse));
    }

    public PageResponse<SubmissionDetailResponse> getSubmissions(Long courseId, Long assignmentId, Long instructorId, Pageable pageable) {
        log.debug("Fetching submissions for assignmentId={}, courseId={}, instructorId={}", assignmentId, courseId, instructorId);
        verifyOwnership(courseId, instructorId);
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + assignmentId));
        if (!assignment.getCourse().getId().equals(courseId))
            throw new UnauthorizedException("Assignment does not belong to this course");

        return PageResponse.of(submissionRepository.findPageByAssignment_Id(assignmentId, pageable)
                .map(s -> submissionMapper.toDetail(s, gradeRepository.findBySubmission_Id(s.getId()))));
    }


    @Transactional
    public SubmissionDetailResponse gradeSubmission(Long submissionId, Long instructorId, GradeSubmissionRequest request) {
        log.info("Instructor {} grading submissionId={}", instructorId, submissionId);
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found: " + submissionId));

        if (!submission.getAssignment().getCourse().getInstructor().getId().equals(instructorId))
            throw new UnauthorizedException("This submission does not belong to your course");

        if (gradeRepository.findBySubmission_Id(submissionId).isPresent())
            throw new ResourceNotFoundException("This submission has already been graded");

        Grade grade = Grade.builder()
                .score(request.score())
                .feedback(request.feedback())
                .submission(submission)
                .student(submission.getStudent())
                .gradedBy(userRepository.getReferenceById(instructorId))
                .build();
        gradeRepository.save(grade);

        submission.setStatus(SubmissionStatus.GRADED);
        submissionRepository.save(submission);

        FeedbackDetail feedbackDetail = FeedbackDetail.builder()
                .marks(String.valueOf(request.score()))
                .feedback(request.feedback())
                .assignmentName(submission.getAssignment().getTitle())
                .courseName(submission.getAssignment().getCourse().getTitle())
                .build();
        emailService.sendFeedbackMail(
                submission.getStudent().getEmail(),
                submission.getStudent().getFirstname(),
                feedbackDetail
        );

        return submissionMapper.toDetail(submission, gradeRepository.findBySubmission_Id(submissionId));
    }

    private Course verifyOwnership(Long courseId, Long instructorId) {
        Course course = courseRepository.findByIdWithInstructor(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
        if (!course.getInstructor().getId().equals(instructorId))
            throw new UnauthorizedException("You are not the instructor of this course");
        return course;
    }

    private ContentType resolveContentType(String mimeType) {
        if (mimeType == null) return ContentType.DOCUMENT;
        return switch (mimeType) {
            case "application/pdf" -> ContentType.PDF;
            case "video/mp4", "video/avi", "video/mkv" -> ContentType.VIDEOS;
            default -> ContentType.DOCUMENT;
        };
    }
}

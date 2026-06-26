package com.example.cohortplatform.service;

import com.example.cohortplatform.dto.message.SessionEvent;
import com.example.cohortplatform.dto.request.CreateSessionRequest;
import com.example.cohortplatform.dto.request.SendMessageRequest;
import com.example.cohortplatform.dto.response.ChatMessageResponse;
import com.example.cohortplatform.dto.response.SessionResponse;
import com.example.cohortplatform.entities.Course;
import com.example.cohortplatform.entities.LiveSession;
import com.example.cohortplatform.entities.SessionQuestion;
import com.example.cohortplatform.entities.User;
import com.example.cohortplatform.exception.ResourceNotFoundException;
import com.example.cohortplatform.exception.UnauthorizedException;
import com.example.cohortplatform.mapper.SessionMapper;
import com.example.cohortplatform.mapper.UserMapper;
import com.example.cohortplatform.repository.CourseEnrollmentRepository;
import com.example.cohortplatform.repository.CourseRepository;
import com.example.cohortplatform.repository.LiveSessionRepository;
import com.example.cohortplatform.repository.SessionQuestionRepository;
import com.example.cohortplatform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveSessionService {

    private final LiveSessionRepository sessionRepository;
    private final SessionQuestionRepository messageRepository;
    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    private final SessionMapper sessionMapper;
    private final UserMapper userMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public SessionResponse createSession(Long courseId, Long instructorId, CreateSessionRequest request) {
        log.info("Instructor {} creating session for courseId={}", instructorId, courseId);
        Course course = courseRepository.findByIdWithInstructor(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
        if (!course.getInstructor().getId().equals(instructorId))
            throw new UnauthorizedException("You are not the instructor of this course");

        User instructor = userRepository.getReferenceById(instructorId);
        LiveSession session = LiveSession.builder()
                .title(request.title())
                .scheduledAt(request.scheduledAt())
                .course(course)
                .instructor(instructor)
                .build();
        return sessionMapper.toResponse(sessionRepository.save(session));
    }

    @Transactional
    public SessionResponse startSession(Long sessionId, Long instructorId) {
        log.info("Instructor {} starting sessionId={}", instructorId, sessionId);
        LiveSession session = verifyInstructor(sessionId, instructorId);
        if (session.isActive())
            throw new IllegalStateException("Session is already active");
        session.setActive(true);
        session.setStartedAt(LocalDateTime.now());
        SessionResponse response = sessionMapper.toResponse(sessionRepository.save(session));
        messagingTemplate.convertAndSend("/topic/session/" + sessionId, SessionEvent.sessionStarted());
        return response;
    }

    @Transactional
    public SessionResponse endSession(Long sessionId, Long instructorId) {
        log.info("Instructor {} ending sessionId={}", instructorId, sessionId);
        LiveSession session = verifyInstructor(sessionId, instructorId);
        if (!session.isActive())
            throw new IllegalStateException("Session is not active");
        session.setActive(false);
        session.setEndedAt(LocalDateTime.now());
        SessionResponse response = sessionMapper.toResponse(sessionRepository.save(session));
        messagingTemplate.convertAndSend("/topic/session/" + sessionId, SessionEvent.sessionEnded());
        return response;
    }

    @Transactional
    public ChatMessageResponse sendMessage(Long sessionId, Long userId, SendMessageRequest request) {
        log.info("User {} sending message in sessionId={}", userId, sessionId);
        LiveSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionId));

        if (!session.isActive())
            throw new IllegalStateException("Session is not currently active");

        if (!enrollmentRepository.existsByCourse_IdAndUser_Id(session.getCourse().getId(), userId)
                && !session.getInstructor().getId().equals(userId))
            throw new UnauthorizedException("You are not part of this course");

        User sender = userRepository.getReferenceById(userId);
        SessionQuestion message = SessionQuestion.builder()
                .messageText(request.messageText())
                .session(session)
                .sentBy(sender)
                .build();
        SessionQuestion saved = messageRepository.save(message);

        ChatMessageResponse response = ChatMessageResponse.builder()
                .messageId(saved.getId())
                .text(saved.getMessageText())
                .sentAt(saved.getSentAt())
                .sentBy(userMapper.toSummary(saved.getSentBy()))
                .build();

        messagingTemplate.convertAndSend("/topic/session/" + sessionId, SessionEvent.chatMessage(response));
        log.debug("Broadcast CHAT_MESSAGE to /topic/session/{}", sessionId);

        return response;
    }

    private LiveSession verifyInstructor(Long sessionId, Long instructorId) {
        LiveSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionId));
        if (!session.getInstructor().getId().equals(instructorId))
            throw new UnauthorizedException("You are not the instructor of this session");
        return session;
    }
}

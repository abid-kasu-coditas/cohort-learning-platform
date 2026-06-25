package com.example.cohortplatform.service;

import com.example.cohortplatform.dto.request.FeedbackDetail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;


    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendCourseWelcomeMail(String to, String name, String courseName) {
        String subject = "Welcome to " + courseName;
        String body = "<h2>Welcome, " + name + "!</h2>"
                + "<p> You successfully enrolled the</p>" + courseName;
        sendHtml(to, subject, body);
    }

    public void sendCourseAssignmentMail(String to, String instructorName, String courseName) {
        String subject = "New Course Assignment" + courseName;
        String body = "<h2>Welcome, " + instructorName + "!</h2>"
                + "<p> You have been assigned to </p>" + courseName;
        sendHtml(to, subject, body);
    }

    public void sendFeedbackMail(String to, String name, FeedbackDetail request) {
        String subject = "Assignment Feedback";
        String body = "<h2>Hello , " + name + "!</h2>"
//                + "<p> You have been assigned to </p>"+ courseName;
                + "<p> You marks has been released of assignment </p>" + "<h1>" + request.assignmentName() + "</h1>" + "<p> You are marks are </p>" + request.marks() + "<p> for Course</p>" + request.courseName();
        sendHtml(to, subject, body);
    }

    private void sendHtml(String to, String subject, String body) {
        try {
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setFrom(fromEmail);
            javaMailSender.send(msg);
            log.info("Email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    public void sendAnnouncementMail(String email, String firstname, @Nullable String announcementTitle, String courseName) {
        String subject = "New Announcement in " + courseName;
        String body = "<h2>Hi " + firstname + ",</h2><p>A new announcement <b>" + announcementTitle + "</b> has been posted in <b>" + courseName + "</b>.</p>";
        sendHtml(email, subject, body);
    }

    public void sendAssignmentNotificationMail(String email, String firstname, String assignmentTitle, String courseName, String deadline) {
        String subject = "New Assignment: " + assignmentTitle + " in " + courseName;
        String body = "<h2>Hi " + firstname + ",</h2><p>A new assignment <b>" + assignmentTitle + "</b> has been posted in <b>" + courseName + "</b>. Please submit before <b>" + deadline + "</b>.</p>";
        sendHtml(email, subject, body);
    }

}




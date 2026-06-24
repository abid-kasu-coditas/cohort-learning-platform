package com.example.cohortplatform.service;

import com.example.cohortplatform.dto.request.FeedbackDetail;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private void sendCourseWelcomeMail(String to,String name,String courseName){
        String subject = "Welcome to " +  courseName;
        String body = "<h2>Welcome, " + name + "!</h2>"
                + "<p> You successfully enrolled the</p>"+ courseName;
        sendHtml(to, subject, body);
    }

    private void sendCourseAssignmentMail(String to,String instructorName,String courseName){
        String subject = "New Course Assignment" +  courseName;
        String body = "<h2>Welcome, " + instructorName + "!</h2>"
                + "<p> You have been assigned to </p>"+ courseName;
        sendHtml(to, subject, body);
    }

    private void sendFeedbackMail(String to, String name, FeedbackDetail request){
        String subject = "Assignment Feedback";
        String body = "<h2>Hello , " + name + "!</h2>"
//                + "<p> You have been assigned to </p>"+ courseName;
                +  "<p> You marks has been released of assignment </p>" +"<h1>"+ request.assignmentName() + "</h1>"+ "<p> You are marks are </p>" + request.marks() + "<p> for Course</p>" + request.courseName();
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
}




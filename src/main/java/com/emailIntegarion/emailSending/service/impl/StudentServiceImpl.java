package com.emailIntegarion.emailSending.service.impl;

import com.emailIntegarion.emailSending.entity.Student;
import com.emailIntegarion.emailSending.repository.StudentRepository;
import com.emailIntegarion.emailSending.service.StudentService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class StudentServiceImpl implements StudentService {
    private static final long OTP_EXPIRATION_MINUTES = 10;

    @Autowired
    StudentRepository repository;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    private final Logger logger= LoggerFactory.getLogger(StudentServiceImpl.class);

    @Override
    public void createSaveStudent(Student student) {
       Student save= repository.save(student);
        SimpleMailMessage message=new SimpleMailMessage();

      //  SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(save.getEmail());
        message.setSubject("Student Information");
        message.setText("Hello " + save.getName() + ",\nYour student record has been saved.");
        javaMailSender.send(message);


    }

    @Override
    public Student getUserStudent(int id) throws MessagingException {

        logger.debug("this is debug string info");
        Student student = repository.getById(id);

        logger.info("Inside getUserStudent class with {}",student.getName());
        // 1. Prepare Thymeleaf context
        Context context = new Context();
        
        context.setVariable("name" ,student.getName());
        context.setVariable("id",student.getId());
        context.setVariable("student",student);

     //   templateEngine.process("student-email-template", context);
        logger.info("Inside getUserStudent template engin  {}",student.getName());
//        context.setVariable("student", student);
//
//        // 2. Load and process Thymeleaf template
            String htmlContent = templateEngine.process("student-show", context);

        // 3. Create MimeMessage with HTML
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(student.getEmail());
        helper.setSubject("Student Information");
        helper.setText(htmlContent, true); // true for HTML content

        javaMailSender.send(mimeMessage);

        logger.info("Inside getUserStudent mail send to {}",student.getName());
        return student;

    }

    @Override
    public void sendPromotionalEmail(int id) throws MessagingException {
        Student student = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Prepare Thymeleaf context
        Context context = new Context();
        context.setVariable("name", student.getName());
        context.setVariable("id", student.getId());
        context.setVariable("promoMessage", "Join our exclusive webinar on Spring Boot next week!");

        // Process HTML template
        String htmlContent = templateEngine.process("promotional-email-template", context);

        // Create MIME message with embedded image
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(student.getEmail());
        helper.setSubject("🎉 Welcome & Exciting News!");
        helper.setText(htmlContent, true); // true = HTML email

        // Add an embedded image (logo, banner, etc.)
        ClassPathResource image = new ClassPathResource("static/image/email-show.png");
        helper.addInline("banner", image); // "banner" must match the CID in HTML

        javaMailSender.send(mimeMessage);
        logger.info("Inside getUserStudent mail send to {}",student.getName());
    }


    @Override
    public void sendEmailWithPdfAttachment(int id) throws MessagingException {
        Student student = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Prepare the email content
        Context context = new Context();
        context.setVariable("name", student.getName());
        context.setVariable("id", student.getId());

        String htmlContent = templateEngine.process("student-pdf-email-template", context);

        // Create the MimeMessage
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setTo(student.getEmail());
        helper.setSubject("Student Report PDF Attached");
        helper.setText(htmlContent, true); // HTML enabled

        // Attach PDF from resources folder or anywhere in file system
        ClassPathResource pdfFile = new ClassPathResource("static/pdf/sample-pdf-file.pdf");
        helper.addAttachment("Student_Report.pdf", pdfFile);

        javaMailSender.send(mimeMessage);
        logger.info("Inside getUserStudent mail send to {}",student.getName());
    }


    @Override
    public void sendBulkStudentEmail(List<Integer> studentIds) throws MessagingException {
        List<Student> students = repository.findAllById(studentIds);

        for (Student student : students) {
            // Prepare Thymeleaf context for each student
            Context context = new Context();
            context.setVariable("name", student.getName());
            context.setVariable("id", student.getId());
            context.setVariable("student", student);

            String htmlContent = templateEngine.process("student-bulk-email-template", context);

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(student.getEmail());
            helper.setSubject("Bulk Email: Student Info");
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
            logger.info("Bulk email sent to: {}", student.getEmail());
        }
    }

    @Override
    public void otpsendemail(int id) throws MessagingException {
        Student student = repository.findById(id).orElseThrow(() -> new RuntimeException(" id not found"));

        SecureRandom secureRandom = new SecureRandom();
        int rawOtp = secureRandom.nextInt(1_000_000);
        String otp = String.format("%06d", rawOtp);

        student.setOtp(otp);
        student.setOtpGeneratedAt(LocalDateTime.now());         // stamp it
        repository.save(student);

        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, false, "UTF-8");
        helper.setTo(student.getEmail());
        helper.setSubject("Your One‑Time Password (OTP)");
        helper.setText(
                "<p>Hi " + student.getName() + ",</p>" +
                        "<p>Your OTP is: <b>" + otp + "</b></p>" +
                        "<p>This code will expire in 10 minutes.</p>",
                true
        );
        javaMailSender.send(msg);


    }


    @Override
    public boolean verifyOtp(int id, String submittedOtp) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student id " + id + " not found"));

        // 1. Has an OTP been generated?
        if (student.getOtp() == null || student.getOtpGeneratedAt() == null) {
            throw new RuntimeException("No OTP request found for student " + id);
        }

        // 2. Check expiration
        LocalDateTime expiresAt = student.getOtpGeneratedAt()
                .plusMinutes(OTP_EXPIRATION_MINUTES);
        if (LocalDateTime.now().isAfter(expiresAt)) {
            // optionally clear the expired OTP
            student.setOtp(null);
            student.setOtpGeneratedAt(null);
            repository.save(student);
            throw new RuntimeException("OTP has expired. Please request a new one.");
        }

        // 3. Check match
        if (!student.getOtp().equals(submittedOtp)) {
            throw new RuntimeException("Invalid OTP provided.");
        }

        // 4. Success! Clear OTP so it can’t be reused
        student.setOtp(null);
        student.setOtpGeneratedAt(null);
        repository.save(student);

        return true;
    }


    @Override
    public void sendVerificationEmail(int id) throws MessagingException {
        Student student = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Generate a random token
        String token = UUID.randomUUID().toString();
        student.setVerificationToken(token);
        repository.save(student);

        // Build verification link
        String verificationLink = "http://localhost:8081/student/verify?token=" + token;

        Context context = new Context();
        context.setVariable("name", student.getName());
        context.setVariable("verificationLink", verificationLink);

        String htmlContent = templateEngine.process("verification-email-template", context);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(student.getEmail());
        helper.setSubject("Email Verification");
        helper.setText(htmlContent, true);

        javaMailSender.send(mimeMessage);
        logger.info("Inside getUserStudent mail send to {}",student.getName());
    }

    @Override
    public String verifyStudentToken(String token) {
        Optional<Student> optionalStudent = repository.findByVerificationToken(token);

        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            student.setVerified(true);
            student.setVerificationToken(null);
            repository.save(student);
            return "Email verified successfully!";
        } else {
            logger.error("Inside verifyStudentToken {}",token);
            return "Invalid or expired verification link.";
        }
    }



}

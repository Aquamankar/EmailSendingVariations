package com.emailIntegarion.emailSending.controller;

import com.emailIntegarion.emailSending.entity.Student;
import com.emailIntegarion.emailSending.service.StudentService;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;


    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);


    @GetMapping("/form")
    public String showStudentForm(Model model) {
        model.addAttribute("student", new Student());
        return "student-form";
    }

    @PostMapping("/create")
    public String cretaeStudent(@ModelAttribute Student student, ModelMap map) {
        studentService.createSaveStudent(student);
        return "redirect:/student/form";
    }

    @GetMapping("/id/{id}")
    public String getStudent(@PathVariable int id, Model map) throws MessagingException {
        logger.info("Inside getStudent method with id: {}", id);
        Student student = studentService.getUserStudent(id);
        logger.info("serevice layer out {}", id);
        map.addAttribute("student", student);
        return "student-show";
    }

    // MIME HTML email with image
    @GetMapping("/mime-email/{id}")
    public String sendMimeEmail(@PathVariable int id) throws MessagingException {
        logger.info("mime-email start {}", id);
        studentService.sendPromotionalEmail(id);
        logger.info("serevice layer out {}", id);
        return "promotional-email-template";
    }



    @GetMapping("/pdf-email/{id}")
    public String sendPdfAttachEmail(@PathVariable int id) throws MessagingException {
        logger.info("with pdf mail {} ", id);
        studentService.sendEmailWithPdfAttachment(id);
        logger.info("serevice layer out {}", id);
        return "student-pdf-email-template";
    }


    @GetMapping("/bulk-email-form")
    public String showBulkEmailForm() {
        return "bulk-email-form"; // HTML form
    }

    @PostMapping("/bulk-email")
    public String sendBulkEmails(@RequestParam("ids") String ids, ModelMap map) throws MessagingException {
        try {
            // Convert comma-separated string to List<Integer>
            List<Integer> studentIds = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            studentService.sendBulkStudentEmail(studentIds);
            map.addAttribute("message", "Bulk email sent successfully!");
        } catch (Exception e) {
            map.addAttribute("message", "Error sending bulk emails: " + e.getMessage());
        }
        return "bulk-email-result";  // You can create this page to show the result
    }


    // ✅ Send verification email
    @GetMapping("/send-verification/{id}")
    public String sendVerificationEmail(@PathVariable int id, ModelMap map) throws MessagingException {
        logger.info("send verification mail {} ", id);
        studentService.sendVerificationEmail(id);
        logger.info("serevice layer out ");
        map.addAttribute("message", "Verification email sent to student.");
        logger.info("go to page  email-verification-sent");
        return "email-verification-sent";
    }

    // ✅ Handle verification link
    @GetMapping("/verify")
    public String verifyStudent(@RequestParam("token") String token, ModelMap map) {
        String resultMessage = studentService.verifyStudentToken(token);
        logger.info("serevice layer out with resultmessage :{} ", resultMessage);
        map.addAttribute("message", resultMessage);
        logger.info("go to page  verification-result");
        return "verification-result";
    }

//    @GetMapping("/otp-send/{id}")
//    public String sendOTPEmail(@PathVariable int id, ModelMap map) throws MessagingException {
//        logger.info("send otp mail id: {} ",id);
//        studentService.otpsendemail(id);
//        logger.info("serevice layer  otp mailout ");
//        map.addAttribute("message", "Verification email sent to student.");
//        logger.info("go to page  email-verification-sent");
//        return "email-verification-sent";


    @GetMapping("/otp-id/{id}")
    public String sendOtpEmail(@PathVariable int id) throws MessagingException {
        studentService.otpsendemail(id);
        return "redirect:/student/verify-otp/" + id;
    }

    /**
     * Show form for user to enter the OTP
     */
    @GetMapping("/verify-otp/{id}")
    public String showOtpForm(@PathVariable int id, Model model) {
        model.addAttribute("studentId", id);
        return "otp-form";  // Thymeleaf template name
    }


    @PostMapping("/verify")
    public String verifyOtp(
            @RequestParam("id") int id,
            @RequestParam("otp") String otp,
            Model model) {
        try {
            studentService.verifyOtp(id, otp);
            model.addAttribute("message", "OTP verified successfully! Proceed to next step.");
            return "verification-success";  // success view
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("studentId", id);
            return "otp-form";  // back to form on error
        }
    }






}




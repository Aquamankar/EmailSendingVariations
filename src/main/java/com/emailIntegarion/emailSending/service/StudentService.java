package com.emailIntegarion.emailSending.service;

import com.emailIntegarion.emailSending.entity.Student;
import jakarta.mail.MessagingException;

import java.util.List;

public interface StudentService {
    void createSaveStudent(Student student);

    Student getUserStudent(int id) throws MessagingException;
    public void sendVerificationEmail(int studentId) throws MessagingException;
    public String verifyStudentToken(String token);
    public void sendPromotionalEmail(int id) throws MessagingException;
    public void sendEmailWithPdfAttachment(int id) throws MessagingException;

    void otpsendemail(int id)throws  MessagingException;
    public void sendBulkStudentEmail(List<Integer> studentIds) throws MessagingException;
    public boolean verifyOtp(int id, String submittedOtp);
}

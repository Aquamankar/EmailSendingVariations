package com.emailIntegarion.emailSending.repository;

import com.emailIntegarion.emailSending.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student,Integer> {
    Optional<Student> findByVerificationToken(String token);
}

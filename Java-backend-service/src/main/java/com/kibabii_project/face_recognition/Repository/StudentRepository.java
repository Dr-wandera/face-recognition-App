package com.kibabii_project.face_recognition.Repository;

import com.kibabii_project.face_recognition.Model.Students;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Students,Long> {
    Optional<Students> findByStudentEmail(String studentEmail);
    Optional<Students> findByRegNumber(String regNumber);
}

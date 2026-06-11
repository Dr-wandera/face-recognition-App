package com.kibabii_project.face_recognition.Repository;

import com.kibabii_project.face_recognition.Dto.StudentRequest;
import com.kibabii_project.face_recognition.Model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Long> {
    Optional<Admin> findByEmployeeNumber(String employeeNumber);
}

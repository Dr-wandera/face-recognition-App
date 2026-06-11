package com.kibabii_project.face_recognition.service;

import com.kibabii_project.face_recognition.Dto.StudentRequest;
import com.kibabii_project.face_recognition.Dto.StudentResponse;
import com.kibabii_project.face_recognition.Dto.VerifyRequest;

import java.util.List;

public interface StudentServiceInterface {
    String registerStudent(StudentRequest studentRequest);

    String verifyStudent(VerifyRequest verifyRequest);

    StudentResponse updateStudentProfile(String regNumber, StudentRequest studentRequest);

    String deleteStudentProfile(String regNumber);

    StudentResponse getStudentByRegNumber(String regNumber);

    List<StudentResponse> getAllStudent(int page, int size);
}

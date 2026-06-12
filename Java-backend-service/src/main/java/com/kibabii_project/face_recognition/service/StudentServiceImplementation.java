package com.kibabii_project.face_recognition.service;

import com.kibabii_project.face_recognition.Dto.StudentRequest;
import com.kibabii_project.face_recognition.Dto.StudentResponse;
import com.kibabii_project.face_recognition.Dto.VerificationResponse;
import com.kibabii_project.face_recognition.Dto.VerifyRequest;
import com.kibabii_project.face_recognition.Model.Students;
import com.kibabii_project.face_recognition.Repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImplementation implements StudentServiceInterface{
    private final StudentRepository studentRepository;
    private final WebClient.Builder webClientBuilder;
    private final String PYTHON_URL = "http://localhost:5000"; //url path that  python api  is running on


    //create student
    @Override
    public String registerStudent(StudentRequest studentRequest) {

        Optional<Students> existingStudent =
                studentRepository.findByStudentEmail(studentRequest.getStudentEmail());

        if (existingStudent.isPresent()) {
            throw new RuntimeException("Email already exists. Check and try again.");
        }

        Optional<Students> existingByRegNumber=studentRepository.findByRegNumber(studentRequest.getRegNumber());
        if (existingByRegNumber.isPresent()) {
            throw new RuntimeException("RegNumber already  taken. Check and try again.");
        }

        if (studentRequest.getImage() == null || studentRequest.getImage().isEmpty()) {
            throw new RuntimeException("Image is required");
        }



        // create object of student
        Students student = Students.builder()
                .firstName(studentRequest.getFirstName())
                .lastName(studentRequest.getLastName())
                .regNumber(studentRequest.getRegNumber())
                .studentEmail(studentRequest.getStudentEmail())
                .faceEncoding(studentRequest.getImage())
                .faculty(studentRequest.getFaculty())
                .department(studentRequest.getDepartment())
                .yearOfStudy(studentRequest.getYearOfStudy())
                .build();

        studentRepository.save(student);
        return "Registered successfully ";
    }

    //verify student face image
    @Override
    public VerificationResponse verifyStudent(VerifyRequest verifyRequest) {

        // Validate input
        if (verifyRequest.getImage() == null || verifyRequest.getImage().isEmpty()) {
            throw new RuntimeException("Verification image is required");
        }

        // Fetch student from database
        Students student = studentRepository.findByRegNumber(verifyRequest.getRegNumber())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Prepare request for Python
        Map<String, String> body = new HashMap<>();
        body.put("registeredImage", student.getFaceEncoding());
        body.put("newImage", verifyRequest.getImage());

        Map response;

        //this code communicate to Python API to compare newly captured image and the one from database (webClient)
        try {
            response = webClientBuilder.build()
                    .post()
                    .uri(PYTHON_URL + "/compare")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Face verification service failed: " + e.getMessage());
        }

        System.out.println("Python response: " + response);

// Validate response
        if (response == null || !response.containsKey("match")) {
            throw new RuntimeException("Invalid response from face verification service");
        }

        boolean isMatch = Boolean.TRUE.equals(response.get("match"));

        return new VerificationResponse(
                isMatch,
                isMatch
                        ? "Eligible to sit for exam"
                        : "Not eligible to sit for exam"
        );

    }

    @Override
    public StudentResponse updateStudentProfile(String regNumber, StudentRequest studentRequest) {
        // check if student with reg Number exist
        Students student = studentRepository.findByRegNumber(regNumber)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (Objects.nonNull(student.getFirstName())&& !student.getFirstName().isEmpty()) {
            student.setFirstName(studentRequest.getFirstName());
        }

        if (Objects.nonNull(student.getLastName())&& !student.getLastName().isEmpty()) {
            student.setLastName(studentRequest.getLastName());
        }

        if (Objects.nonNull(student.getFaceEncoding())&& !student.getFaceEncoding().isEmpty()) {
            student.setFaceEncoding(studentRequest.getImage());
        }
        if (Objects.nonNull(student.getRegNumber())&& !student.getRegNumber().isEmpty()) {
            student.setFaceEncoding(studentRequest.getRegNumber());
        }

       var updateProfile= studentRepository.save(student);
        return toDto(updateProfile);

   }

   //delete student profile
    @Override
    public String deleteStudentProfile(String regNumber) {
        Students students=studentRepository.findByRegNumber(regNumber)
                .orElseThrow(()->new RuntimeException("Student not found"));
        studentRepository.delete(students);
        return "Deleted student successfully";
    }

    //find single student by reg number
    @Override
    public StudentResponse getStudentByRegNumber(String regNumber) {
        Students students=studentRepository.findByRegNumber(regNumber)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return toDto(students);
    }

    //this method return list of student jsb
    @Override
    public List<StudentResponse> getAllStudent(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return studentRepository.findAll(pageable)
                .stream()
                .map(this::toDto)
                .toList();
    }

    //Convert Entity-> Dto
    private StudentResponse toDto(Students updateProfile) {
        StudentResponse response=new StudentResponse();
        response.setFirstName(updateProfile.getFirstName());
        response.setLastName(updateProfile.getLastName());
        response.setRegNumber(updateProfile.getRegNumber());
        response.setDepartment(updateProfile.getDepartment());
        response.setFaculty(updateProfile.getFaculty());
        response.setYearOfStudy(updateProfile.getYearOfStudy());
        return response;
    }
}

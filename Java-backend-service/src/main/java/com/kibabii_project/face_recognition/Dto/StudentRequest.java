package com.kibabii_project.face_recognition.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentRequest {
    private String firstName;
    private String lastName;
    private String regNumber;
    private String image;
    private String studentEmail;
    private String department;
    private String faculty;
    private String yearOfStudy;

}

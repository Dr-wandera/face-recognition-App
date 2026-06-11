package com.kibabii_project.face_recognition.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponse {
    private String regNumber;
    private String lastName;
    private String  firstName;
    private String department;
    private String faculty;
    private String yearOfStudy;
}

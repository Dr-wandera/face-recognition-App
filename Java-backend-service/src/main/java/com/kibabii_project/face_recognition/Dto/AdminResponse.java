package com.kibabii_project.face_recognition.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminResponse {
    private String firstName;
    private String lastName;
    private String employeeNumber;
    private String phoneNumber;
    private String department;
    private String position;
    private String email;
    private LocalDateTime createTime;
}

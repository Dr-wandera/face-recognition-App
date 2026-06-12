package com.kibabii_project.face_recognition.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationResponse {
    private boolean match;
    private String message;
}

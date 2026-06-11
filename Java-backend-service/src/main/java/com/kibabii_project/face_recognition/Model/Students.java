package com.kibabii_project.face_recognition.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Students {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "f.name")
    private String firstName;
    @Column(name = "l.name")
    private String lastName;
    @Column(name = "reg_number", unique = true)
    private String regNumber;
    @Column(name = "email", unique = true)
    private String studentEmail;
    private String department;
    private String yearOfStudy;
    private String faculty;

    @Column(name = "image", columnDefinition = "LONGTEXT")
    private String faceEncoding;

}

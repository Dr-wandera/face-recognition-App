package com.kibabii_project.face_recognition.Controller;

import com.kibabii_project.face_recognition.Dto.LoginRequest;
import com.kibabii_project.face_recognition.service.LoginServiceImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private final LoginServiceImplementation loginServiceImplementation;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        return loginServiceImplementation.login(loginRequest);

    }
}

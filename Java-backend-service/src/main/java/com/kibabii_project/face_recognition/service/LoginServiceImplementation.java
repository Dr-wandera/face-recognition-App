package com.kibabii_project.face_recognition.service;

import com.kibabii_project.face_recognition.Dto.LoginRequest;
import com.kibabii_project.face_recognition.Dto.LoginResponse;
import com.kibabii_project.face_recognition.Model.User;
import com.kibabii_project.face_recognition.Repository.UserRepository;
import com.kibabii_project.face_recognition.Security.AppUserDetailsService;
import com.kibabii_project.face_recognition.Security.Jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoginServiceImplementation {
    private final UserRepository userRepository;
    private final AppUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> login(LoginRequest loginRequest) {
        
        User user=userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()->new RuntimeException("invalid email! check and try again "));

        /*
              .this check first if user is verified in if not, not allowed to log in
              .this  prevents log in if you have not verified the account
         */

        log.info("logged in:{}", loginRequest);
        try {
            authenticate(loginRequest.getEmail(), loginRequest.getPassword());

            final UserDetails userDetails =
                    userDetailsService.loadUserByUsername(loginRequest.getEmail());

            String jwtToken = jwtService.generateToken(userDetails);

            return ResponseEntity.ok()
                    .body(new LoginResponse(loginRequest.getEmail(), jwtToken));

        } catch (BadCredentialsException ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", true);
            response.put("message", "Incorrect email or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( response);

        }
        catch (DisabledException ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", true);
            response.put("message", "account is disabled");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body( response);

        }
        catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", true);
            response.put("message", "Authentication Failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body( response);

        }
    }
    private void authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }
}

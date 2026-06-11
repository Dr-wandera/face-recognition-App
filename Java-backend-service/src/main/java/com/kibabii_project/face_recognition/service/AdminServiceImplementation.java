package com.kibabii_project.face_recognition.service;

import com.kibabii_project.face_recognition.Dto.AdminRequest;
import com.kibabii_project.face_recognition.Dto.AdminResponse;
import com.kibabii_project.face_recognition.Model.Admin;
import com.kibabii_project.face_recognition.Model.Role;
import com.kibabii_project.face_recognition.Model.User;
import com.kibabii_project.face_recognition.Repository.AdminRepository;
import com.kibabii_project.face_recognition.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImplementation implements AdminServiceInterface{
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AdminResponse createAdmin(AdminRequest adminRequest) {
        if (userRepository.existsByEmail(adminRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        //save user to the database (Admin)
        User user = toEntity(adminRequest);

        //Saves admin to admin entity(admin Info)
        Admin admin = Admin.builder()
                .firstName(adminRequest.getFirstName())
                .lastName(adminRequest.getLastName())
                .employeeNumber(adminRequest.getEmployeeNumber())
                .phoneNumber(adminRequest.getPhoneNumber())
                .department(adminRequest.getDepartment())
                .position(adminRequest.getPosition())
                .email(adminRequest.getEmail())
                .createTime(LocalDateTime.now())
                .user(user)
                .build();

        Admin savedAdmin = adminRepository.save(admin);
        userRepository.save(user);

        return toDto(savedAdmin);
    }

    //convert entity->dto
    private AdminResponse toDto(Admin savedAdmin) {

        AdminResponse response = new AdminResponse();
        response.setDepartment(savedAdmin.getDepartment());
        response.setPosition(savedAdmin.getPosition());
        response.setFirstName(savedAdmin.getFirstName());
        response.setLastName(savedAdmin.getLastName());
        response.setEmployeeNumber(savedAdmin.getEmployeeNumber());
        response.setCreateTime(LocalDateTime.now());
        response.setEmail(savedAdmin.getEmail());
        return response;
    }

    //method that gets all admins
    @Override
    public List<AdminResponse> allAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return adminRepository.findAll(pageable)
                .stream()
                .map(this::toDto)
                .toList();
    }

    //finds admin by employee number
    @Override
    public AdminResponse getAdminByEmployeeNumber(String employeeNumber) {
        Admin admin =adminRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(()->new RuntimeException("Admin with that number does not exist! Check and try again! "));

        return toDto(admin);
    }

    // convert admin request  to user (admin login credential stored in user table. user & admin have oneToOne relationship)
    private User toEntity(AdminRequest adminRequest) {
        User user = new User();
        user.setEmail(adminRequest.getEmail());
        user.setPassword(passwordEncoder.encode(adminRequest.getPassword()));
        user.setRole(Role.ADMIN);
        user.setEnabled(true);
        user.setCreateTime(LocalDateTime.now());
        return user;

    }

}

package com.kibabii_project.face_recognition.service;

import com.kibabii_project.face_recognition.Dto.AdminRequest;
import com.kibabii_project.face_recognition.Dto.AdminResponse;

import java.util.List;

public interface AdminServiceInterface {
    AdminResponse createAdmin(AdminRequest adminRequest);

    List<AdminResponse> allAdmin(int page, int size);

    AdminResponse getAdminByEmployeeNumber(String employeeNumber);
}

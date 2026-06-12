package com.kibabii_project.face_recognition.Controller;

import com.kibabii_project.face_recognition.Dto.*;import com.kibabii_project.face_recognition.Model.Admin;
import com.kibabii_project.face_recognition.service.AdminServiceImplementation;
import com.kibabii_project.face_recognition.service.StudentServiceImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final StudentServiceImplementation studentServiceImplementation;
    private final AdminServiceImplementation adminServiceImplementation;

    @PostMapping("/register")
    public AdminResponse createAdmin(@RequestBody AdminRequest adminRequest){
        return adminServiceImplementation.createAdmin(adminRequest);
    }
    @GetMapping("/allAdmin")
    @ResponseStatus(HttpStatus.OK)
    public List<AdminResponse> getAllAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return adminServiceImplementation.allAdmin(page,size);
    }
    @GetMapping("/get/{employeeNumber}")
    @ResponseStatus(HttpStatus.OK)
    public AdminResponse getAdminByEmployeeNumber(@PathVariable String employeeNumber){
        return adminServiceImplementation.getAdminByEmployeeNumber(employeeNumber);
    }

    @DeleteMapping("/delete/admin/{employeeNumber}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteAdminProfile(@PathVariable String employeeNumber){
        return adminServiceImplementation.deleteAdminProfile(employeeNumber);
    }

    @PutMapping("/update/admin/{employeeNumber}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AdminResponse updateAdminProfile(@PathVariable String employeeNumber, @RequestBody AdminRequest adminRequest){
        return adminServiceImplementation.updateAdminProfile(employeeNumber,adminRequest);
    }

    @PostMapping("/create/student")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String registerStudent(@RequestBody StudentRequest studentRequest){
       return studentServiceImplementation.registerStudent(studentRequest);
    }
    @PostMapping("/verify/student")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public VerificationResponse verifyStudent(@RequestBody VerifyRequest verifyRequest){
        return studentServiceImplementation.verifyStudent(verifyRequest);
    }
    @PutMapping("/update/{regNumber}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public StudentResponse updateResponse(@PathVariable String regNumber, @RequestBody StudentRequest studentRequest){
        return  studentServiceImplementation.updateStudentProfile(regNumber, studentRequest);
    }
    @DeleteMapping("/delete/{regNumber}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteStudent(@PathVariable String regNumber){
        return studentServiceImplementation.deleteStudentProfile(regNumber);
    }
    @GetMapping("/get/{regNumber}")
    @ResponseStatus(HttpStatus.OK)
    public StudentResponse getStudentByRegNumber(@PathVariable String regNumber){
        return studentServiceImplementation.getStudentByRegNumber(regNumber);
    }
    @GetMapping("/allStudents")
    public List<StudentResponse> getAllStudent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return studentServiceImplementation.getAllStudent(page,size);
    }

}

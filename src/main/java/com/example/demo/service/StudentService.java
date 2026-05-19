package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentService {

    @Autowired private StudentRepository studentRepository;

    public Page<Student> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
    }

    public Student getStudentByEnrollment(String enrollmentNumber) {
        return studentRepository.findByEnrollmentNumber(enrollmentNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "enrollmentNumber", enrollmentNumber));
    }

    public Page<Student> getStudentsByDepartment(Long deptId, Pageable pageable) {
        return studentRepository.findByDepartmentId(deptId, pageable);
    }

    public Page<Student> getStudentsBySemester(Integer semester, Pageable pageable) {
        return studentRepository.findByCurrentSemester(semester, pageable);
    }

    public Page<Student> getStudentsByStatus(String status, Pageable pageable) {
        return studentRepository.findByStatus(status, pageable);
    }

    public Page<Student> search(Long departmentId, Integer semester, String status, String keyword, Pageable pageable) {
        return studentRepository.search(departmentId, semester, emptyToNull(status), emptyToNull(keyword), pageable);
    }

    @Transactional
    public Student createStudent(Student student) {
        if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + student.getEmail());
        }
        if (studentRepository.findByEnrollmentNumber(student.getEnrollmentNumber()).isPresent()) {
            throw new IllegalArgumentException("Enrollment number already exists: " + student.getEnrollmentNumber());
        }
        return studentRepository.save(student);
    }

    @Transactional
    public Student updateStudent(Long id, Student details) {
        Student student = getStudentById(id);
        student.setFirstName(details.getFirstName());
        student.setLastName(details.getLastName());
        student.setPhone(details.getPhone());
        student.setAddress(details.getAddress());
        student.setGuardianName(details.getGuardianName());
        student.setGuardianPhone(details.getGuardianPhone());
        student.setStatus(details.getStatus());
        student.setCurrentSemester(details.getCurrentSemester());
        student.setDepartment(details.getDepartment());
        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        getStudentById(id);
        studentRepository.deleteById(id);
    }

    public long countByDepartment(Long deptId) {
        return studentRepository.countByDepartmentId(deptId);
    }

    public long countByStatus(String status) {
        return studentRepository.countByStatus(status);
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}

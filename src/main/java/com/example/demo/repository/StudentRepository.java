package com.example.demo.repository;

import com.example.demo.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    Optional<Student> findByEnrollmentNumber(String enrollmentNumber);
    Page<Student> findByDepartmentId(Long departmentId, Pageable pageable);
    Page<Student> findByStatus(String status, Pageable pageable);
    Page<Student> findByDepartmentIdAndStatus(Long departmentId, String status, Pageable pageable);
    Page<Student> findByCurrentSemester(Integer semester, Pageable pageable);
    long countByDepartmentId(Long departmentId);
    long countByStatus(String status);
}

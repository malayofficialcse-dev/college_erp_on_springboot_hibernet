package com.example.demo.repository;

import com.example.demo.model.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByEmail(String email);
    Optional<Teacher> findByEmployeeCode(String employeeCode);
    Page<Teacher> findByDepartmentId(Long departmentId, Pageable pageable);
    Page<Teacher> findByStatus(String status, Pageable pageable);
    long countByDepartmentId(Long departmentId);
}

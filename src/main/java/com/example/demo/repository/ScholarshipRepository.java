package com.example.demo.repository;

import com.example.demo.model.Scholarship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScholarshipRepository extends JpaRepository<Scholarship, Long> {
    Page<Scholarship> findByStudentId(Long studentId, Pageable pageable);
    Page<Scholarship> findByType(String type, Pageable pageable);
    Page<Scholarship> findByStatus(String status, Pageable pageable);
    Page<Scholarship> findByAcademicYearId(Long academicYearId, Pageable pageable);
    List<Scholarship> findByStudentIdAndStatusIgnoreCase(Long studentId, String status);
    List<Scholarship> findByStudentIdAndAcademicYearIdAndStatusIgnoreCase(Long studentId, Long academicYearId, String status);
}

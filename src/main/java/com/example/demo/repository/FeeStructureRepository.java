package com.example.demo.repository;

import com.example.demo.model.FeeStructure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    Page<FeeStructure> findByCourseId(Long courseId, Pageable pageable);
    Page<FeeStructure> findByAcademicYearId(Long academicYearId, Pageable pageable);
    Optional<FeeStructure> findByCourseIdAndAcademicYearIdAndSemesterNumber(
        Long courseId, Long academicYearId, Integer semesterNumber);
}

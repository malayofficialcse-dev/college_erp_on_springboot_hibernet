package com.example.demo.repository;

import com.example.demo.model.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    Page<Semester> findByAcademicYearId(Long academicYearId, Pageable pageable);
    Optional<Semester> findByIsCurrentTrue();
}

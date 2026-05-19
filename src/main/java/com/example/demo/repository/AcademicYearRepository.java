package com.example.demo.repository;

import com.example.demo.model.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    Optional<AcademicYear> findByYearLabel(String yearLabel);
    Optional<AcademicYear> findByIsCurrentTrue();
}

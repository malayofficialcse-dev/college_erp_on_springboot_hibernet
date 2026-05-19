package com.example.demo.repository;

import com.example.demo.model.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    Page<Exam> findByCourseId(Long courseId, Pageable pageable);
    Page<Exam> findBySemesterId(Long semesterId, Pageable pageable);
    Page<Exam> findByStatus(String status, Pageable pageable);
    Page<Exam> findByExamType(String examType, Pageable pageable);
}

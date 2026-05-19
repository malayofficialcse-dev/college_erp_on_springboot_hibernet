package com.example.demo.repository;

import com.example.demo.model.ExamResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    Page<ExamResult> findByStudentId(Long studentId, Pageable pageable);
    Page<ExamResult> findByExamId(Long examId, Pageable pageable);
    Page<ExamResult> findBySubjectId(Long subjectId, Pageable pageable);
    Page<ExamResult> findByStudentIdAndSemester(Long studentId, String semester, Pageable pageable);
}

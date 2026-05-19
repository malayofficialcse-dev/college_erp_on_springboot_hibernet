package com.example.demo.repository;

import com.example.demo.model.ExamSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, Long> {
    Page<ExamSchedule> findByExamId(Long examId, Pageable pageable);
    Page<ExamSchedule> findBySubjectId(Long subjectId, Pageable pageable);
    Page<ExamSchedule> findByExamCourseIdAndSubjectSemesterNumber(Long courseId, Integer semesterNumber, Pageable pageable);
    List<ExamSchedule> findByExamCourseIdAndSubjectSemesterNumberOrderByExamDateAscStartTimeAsc(Long courseId, Integer semesterNumber);
    List<ExamSchedule> findByExamDateBetweenOrderByExamDateAscStartTimeAsc(LocalDate from, LocalDate to);
}

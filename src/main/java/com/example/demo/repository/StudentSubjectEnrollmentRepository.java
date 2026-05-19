package com.example.demo.repository;

import com.example.demo.model.StudentSubjectEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSubjectEnrollmentRepository extends JpaRepository<StudentSubjectEnrollment, Long> {
    List<StudentSubjectEnrollment> findByStudentIdAndSemesterNumberOrderByIdAsc(Long studentId, Integer semesterNumber);
    List<StudentSubjectEnrollment> findByStudentIdOrderBySemesterNumberAscIdAsc(Long studentId);
    Optional<StudentSubjectEnrollment> findByStudentIdAndSubjectIdAndSemesterNumber(Long studentId, Long subjectId, Integer semesterNumber);
    boolean existsByStudentIdAndSubjectIdAndSemesterNumber(Long studentId, Long subjectId, Integer semesterNumber);
}

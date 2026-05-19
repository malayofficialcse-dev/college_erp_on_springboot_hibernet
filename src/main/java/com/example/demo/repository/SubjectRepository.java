package com.example.demo.repository;

import com.example.demo.model.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findBySubjectCode(String subjectCode);
    Page<Subject> findByCourseId(Long courseId, Pageable pageable);
    Page<Subject> findByTeacherId(Long teacherId, Pageable pageable);
    Page<Subject> findByCourseIdAndSemesterNumber(Long courseId, Integer semesterNumber, Pageable pageable);
}

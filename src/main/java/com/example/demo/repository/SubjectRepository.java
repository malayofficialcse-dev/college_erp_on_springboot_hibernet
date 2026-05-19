package com.example.demo.repository;

import com.example.demo.model.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findBySubjectCode(String subjectCode);
    Page<Subject> findByCourseId(Long courseId, Pageable pageable);
    Page<Subject> findByTeacherId(Long teacherId, Pageable pageable);
    Page<Subject> findByCourseIdAndSemesterNumber(Long courseId, Integer semesterNumber, Pageable pageable);
    List<Subject> findByTeacherId(Long teacherId);
    List<Subject> findByCourseIdAndSemesterNumber(Long courseId, Integer semesterNumber);
    List<Subject> findByCourseIdAndSemesterNumberAndSubjectTypeIgnoreCase(Long courseId, Integer semesterNumber, String subjectType);

    @Query("""
            SELECT s FROM Subject s
            WHERE (:courseId IS NULL OR s.course.id = :courseId)
              AND (:teacherId IS NULL OR s.teacher.id = :teacherId)
              AND (:semesterNumber IS NULL OR s.semesterNumber = :semesterNumber)
              AND (:keyword IS NULL OR
                   LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(s.subjectCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(COALESCE(s.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Subject> search(@Param("courseId") Long courseId,
                         @Param("teacherId") Long teacherId,
                         @Param("semesterNumber") Integer semesterNumber,
                         @Param("keyword") String keyword,
                         Pageable pageable);
}

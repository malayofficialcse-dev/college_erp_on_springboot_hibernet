package com.example.demo.repository;

import com.example.demo.model.ExamResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    Page<ExamResult> findByStudentId(Long studentId, Pageable pageable);
    Page<ExamResult> findByExamId(Long examId, Pageable pageable);
    Page<ExamResult> findBySubjectId(Long subjectId, Pageable pageable);
    Page<ExamResult> findByStudentIdAndSemester(Long studentId, String semester, Pageable pageable);
    List<ExamResult> findTop5ByStudentIdOrderByIdDesc(Long studentId);
    List<ExamResult> findByStudentIdOrderBySemesterAscIdAsc(Long studentId);
    List<ExamResult> findByStudentIdAndSemesterOrderByIdAsc(Long studentId, String semester);

    @Query("""
            SELECT DISTINCT er.semester FROM ExamResult er
            WHERE er.student.id = :studentId AND er.semester IS NOT NULL
            ORDER BY er.semester
            """)
    List<String> findDistinctSemestersByStudentId(@Param("studentId") Long studentId);

    @Query("""
            SELECT d.id,
                   d.name,
                   COUNT(DISTINCT s.id),
                   AVG(er.gradePoint),
                   SUM(CASE WHEN UPPER(er.resultStatus) = 'PASS' THEN 1 ELSE 0 END),
                   COUNT(er)
            FROM ExamResult er
            JOIN er.student s
            JOIN s.department d
            JOIN er.exam ex
            WHERE (:fromDate IS NULL OR ex.startDate >= :fromDate)
              AND (:toDate IS NULL OR ex.startDate <= :toDate)
              AND (:examId IS NULL OR ex.id = :examId)
              AND (:courseId IS NULL OR ex.course.id = :courseId)
            GROUP BY d.id, d.name
            ORDER BY d.name
            """)
    List<Object[]> aggregateDepartmentPerformance(@Param("fromDate") LocalDate fromDate,
                                                 @Param("toDate") LocalDate toDate,
                                                 @Param("examId") Long examId,
                                                 @Param("courseId") Long courseId);
}

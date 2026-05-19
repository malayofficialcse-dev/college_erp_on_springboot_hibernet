package com.example.demo.repository;

import com.example.demo.model.AttendanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {
    Page<AttendanceRecord> findByStudentId(Long studentId, Pageable pageable);
    Page<AttendanceRecord> findBySubjectId(Long subjectId, Pageable pageable);
    Page<AttendanceRecord> findByDate(LocalDate date, Pageable pageable);
    Page<AttendanceRecord> findByStudentIdAndSubjectId(Long studentId, Long subjectId, Pageable pageable);
    List<AttendanceRecord> findByStudentId(Long studentId);
    List<AttendanceRecord> findByTeacherIdAndDate(Long teacherId, LocalDate date);
    long countByTeacherIdAndDate(Long teacherId, LocalDate date);
    @Query("""
            SELECT a FROM AttendanceRecord a
            WHERE (:studentId IS NULL OR a.student.id = :studentId)
              AND (:subjectId IS NULL OR a.subject.id = :subjectId)
              AND (:teacherId IS NULL OR a.teacher.id = :teacherId)
              AND (:status IS NULL OR UPPER(a.status) = UPPER(:status))
              AND (:dateFrom IS NULL OR a.date >= :dateFrom)
              AND (:dateTo IS NULL OR a.date <= :dateTo)
            """)
    Page<AttendanceRecord> search(@Param("studentId") Long studentId,
                                  @Param("subjectId") Long subjectId,
                                  @Param("teacherId") Long teacherId,
                                  @Param("status") String status,
                                  @Param("dateFrom") LocalDate dateFrom,
                                  @Param("dateTo") LocalDate dateTo,
                                  Pageable pageable);

    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.student.id = :studentId AND a.subject.id = :subjectId AND a.status = 'PRESENT'")
    long countPresentByStudentAndSubject(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId);

    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.student.id = :studentId AND a.subject.id = :subjectId")
    long countTotalByStudentAndSubject(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId);

    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.student.id = :studentId AND UPPER(a.status) = 'PRESENT'")
    long countPresentByStudent(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.student.id = :studentId")
    long countTotalByStudent(@Param("studentId") Long studentId);

    @Query("SELECT COUNT(DISTINCT a.timetable.id) FROM AttendanceRecord a WHERE a.teacher.id = :teacherId AND a.date = :date AND a.timetable IS NOT NULL")
    long countDistinctTimetablesMarkedByTeacherOnDate(@Param("teacherId") Long teacherId, @Param("date") LocalDate date);
}

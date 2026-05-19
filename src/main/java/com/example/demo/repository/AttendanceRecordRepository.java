package com.example.demo.repository;

import com.example.demo.model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    @Query("""
            SELECT ar.subject.id,
                   ar.subject.subjectCode,
                   ar.subject.name,
                   COUNT(ar),
                   SUM(CASE WHEN UPPER(ar.status) IN ('PRESENT','LATE','EXCUSED') THEN 1 ELSE 0 END),
                   SUM(CASE WHEN UPPER(ar.status) = 'ABSENT' THEN 1 ELSE 0 END)
            FROM AttendanceRecord ar
            WHERE (:studentId IS NULL OR ar.student.id = :studentId)
              AND (:subjectId IS NULL OR ar.subject.id = :subjectId)
              AND (:fromDate IS NULL OR ar.date >= :fromDate)
              AND (:toDate IS NULL OR ar.date <= :toDate)
            GROUP BY ar.subject.id, ar.subject.subjectCode, ar.subject.name
            ORDER BY ar.subject.subjectCode
            """)
    List<Object[]> aggregateByStudentAndSubject(@Param("studentId") Long studentId,
                                               @Param("subjectId") Long subjectId,
                                               @Param("fromDate") LocalDate fromDate,
                                               @Param("toDate") LocalDate toDate);

    @Query("""
            SELECT d.id,
                   d.name,
                   COUNT(ar),
                   SUM(CASE WHEN UPPER(ar.status) IN ('PRESENT','LATE','EXCUSED') THEN 1 ELSE 0 END)
            FROM AttendanceRecord ar
            JOIN ar.student s
            JOIN s.department d
            WHERE (:fromDate IS NULL OR ar.date >= :fromDate)
              AND (:toDate IS NULL OR ar.date <= :toDate)
            GROUP BY d.id, d.name
            ORDER BY d.name
            """)
    List<Object[]> aggregateByDepartment(@Param("fromDate") LocalDate fromDate,
                                        @Param("toDate") LocalDate toDate);
}

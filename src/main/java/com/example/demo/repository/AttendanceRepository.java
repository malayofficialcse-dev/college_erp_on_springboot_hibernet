package com.example.demo.repository;

import com.example.demo.model.AttendanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {
    Page<AttendanceRecord> findByStudentId(Long studentId, Pageable pageable);
    Page<AttendanceRecord> findBySubjectId(Long subjectId, Pageable pageable);
    Page<AttendanceRecord> findByDate(LocalDate date, Pageable pageable);
    Page<AttendanceRecord> findByStudentIdAndSubjectId(Long studentId, Long subjectId, Pageable pageable);

    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.student.id = :studentId AND a.subject.id = :subjectId AND a.status = 'PRESENT'")
    long countPresentByStudentAndSubject(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId);

    @Query("SELECT COUNT(a) FROM AttendanceRecord a WHERE a.student.id = :studentId AND a.subject.id = :subjectId")
    long countTotalByStudentAndSubject(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId);
}

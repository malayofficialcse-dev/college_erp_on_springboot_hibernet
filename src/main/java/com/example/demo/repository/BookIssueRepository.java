package com.example.demo.repository;

import com.example.demo.model.BookIssue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {
    Page<BookIssue> findByStudentId(Long studentId, Pageable pageable);
    Page<BookIssue> findByEmployeeId(Long employeeId, Pageable pageable);
    Page<BookIssue> findByBookId(Long bookId, Pageable pageable);
    Page<BookIssue> findByStatus(String status, Pageable pageable);
    List<BookIssue> findByStudentIdOrderByIssueDateDesc(Long studentId);
    List<BookIssue> findByEmployeeIdOrderByIssueDateDesc(Long employeeId);
    long countByBookIdAndStatus(Long bookId, String status);

    @Query("SELECT COUNT(bi) FROM BookIssue bi WHERE UPPER(bi.status) = 'ISSUED' AND bi.dueDate < :today")
    long countOverdueIssues(@Param("today") LocalDate today);

    @Query("""
            SELECT bi FROM BookIssue bi
            WHERE bi.returnDate IS NULL
              AND bi.dueDate IS NOT NULL
              AND bi.dueDate < :asOf
              AND UPPER(bi.status) IN ('ISSUED','OVERDUE')
            ORDER BY bi.dueDate ASC, bi.id ASC
            """)
    List<BookIssue> findActiveOverdueAsOf(@Param("asOf") LocalDate asOf);
}

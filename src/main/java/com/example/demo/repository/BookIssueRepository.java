package com.example.demo.repository;

import com.example.demo.model.BookIssue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {
    Page<BookIssue> findByStudentId(Long studentId, Pageable pageable);
    Page<BookIssue> findByEmployeeId(Long employeeId, Pageable pageable);
    Page<BookIssue> findByBookId(Long bookId, Pageable pageable);
    Page<BookIssue> findByStatus(String status, Pageable pageable);
    long countByBookIdAndStatus(Long bookId, String status);
}

package com.example.demo.repository;

import com.example.demo.model.BookReservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookReservationRepository extends JpaRepository<BookReservation, Long> {
    Page<BookReservation> findByStudentId(Long studentId, Pageable pageable);
    Page<BookReservation> findByEmployeeId(Long employeeId, Pageable pageable);
    Page<BookReservation> findByBookId(Long bookId, Pageable pageable);
    List<BookReservation> findByBookIdAndStatusOrderByQueuePositionAsc(Long bookId, String status);
    boolean existsByBookIdAndStudentIdAndStatus(Long bookId, Long studentId, String status);
    boolean existsByBookIdAndEmployeeIdAndStatus(Long bookId, Long employeeId, String status);
}

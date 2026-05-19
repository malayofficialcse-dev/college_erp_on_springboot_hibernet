package com.example.demo.repository;

import com.example.demo.model.FeePayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {
    Page<FeePayment> findByStudentId(Long studentId, Pageable pageable);
    Page<FeePayment> findByStatus(String status, Pageable pageable);
    Page<FeePayment> findByStudentIdAndStatus(Long studentId, String status, Pageable pageable);
    Page<FeePayment> findBySemester(String semester, Pageable pageable);

    @Query("SELECT SUM(f.netAmount) FROM FeePayment f WHERE f.student.id = :studentId AND f.status = 'PAID'")
    BigDecimal sumPaidFeeByStudent(@Param("studentId") Long studentId);

    @Query("SELECT SUM(f.netAmount) FROM FeePayment f WHERE f.status = 'PAID'")
    BigDecimal sumTotalCollected();
}

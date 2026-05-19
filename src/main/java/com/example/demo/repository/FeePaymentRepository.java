package com.example.demo.repository;

import com.example.demo.model.FeePayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {
    Page<FeePayment> findByStudentId(Long studentId, Pageable pageable);
    Page<FeePayment> findByStatus(String status, Pageable pageable);
    Page<FeePayment> findByStudentIdAndStatus(Long studentId, String status, Pageable pageable);
    Page<FeePayment> findBySemester(String semester, Pageable pageable);
    List<FeePayment> findTop5ByStudentIdOrderByPaymentDateDescIdDesc(Long studentId);
    long countByStudentIdAndStatusIgnoreCase(Long studentId, String status);
    long countByStatusIgnoreCase(String status);

    @Query("SELECT SUM(f.netAmount) FROM FeePayment f WHERE f.student.id = :studentId AND f.status = 'PAID'")
    BigDecimal sumPaidFeeByStudent(@Param("studentId") Long studentId);

    @Query("SELECT SUM(f.netAmount) FROM FeePayment f WHERE f.status = 'PAID'")
    BigDecimal sumTotalCollected();

    @Query("SELECT SUM(f.netAmount) FROM FeePayment f WHERE f.status = 'PAID' AND f.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal sumCollectedBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT f FROM FeePayment f
            WHERE (:studentId IS NULL OR f.student.id = :studentId)
              AND (:status IS NULL OR UPPER(f.status) = UPPER(:status))
              AND (:semester IS NULL OR UPPER(f.semester) = UPPER(:semester))
              AND (:dateFrom IS NULL OR f.paymentDate >= :dateFrom)
              AND (:dateTo IS NULL OR f.paymentDate <= :dateTo)
              AND (:keyword IS NULL OR
                   LOWER(COALESCE(f.receiptNumber, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(COALESCE(f.transactionId, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                   LOWER(COALESCE(f.remarks, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<FeePayment> search(@Param("studentId") Long studentId,
                            @Param("status") String status,
                            @Param("semester") String semester,
                            @Param("dateFrom") LocalDate dateFrom,
                            @Param("dateTo") LocalDate dateTo,
                            @Param("keyword") String keyword,
                            Pageable pageable);

    @Query("""
            SELECT YEAR(f.paymentDate),
                   MONTH(f.paymentDate),
                   SUM(f.netAmount),
                   COUNT(f)
            FROM FeePayment f
            WHERE UPPER(f.status) = 'PAID'
              AND (:fromDate IS NULL OR f.paymentDate >= :fromDate)
              AND (:toDate IS NULL OR f.paymentDate <= :toDate)
            GROUP BY YEAR(f.paymentDate), MONTH(f.paymentDate)
            ORDER BY YEAR(f.paymentDate), MONTH(f.paymentDate)
            """)
    List<Object[]> monthlyCollections(@Param("fromDate") LocalDate fromDate,
                                     @Param("toDate") LocalDate toDate);
}

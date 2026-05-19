package com.example.demo.repository;

import com.example.demo.model.Payroll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    Page<Payroll> findByEmployeeId(Long employeeId, Pageable pageable);
    Page<Payroll> findByPayMonthAndPayYear(Integer month, Integer year, Pageable pageable);
    Page<Payroll> findByStatus(String status, Pageable pageable);
    Optional<Payroll> findByEmployeeIdAndPayMonthAndPayYear(Long employeeId, Integer month, Integer year);

    @Query("SELECT SUM(p.netSalary) FROM Payroll p WHERE p.payMonth = :month AND p.payYear = :year AND p.status = 'PAID'")
    BigDecimal sumNetSalaryByMonthAndYear(@Param("month") Integer month, @Param("year") Integer year);

    @Query("""
            SELECT d.id,
                   COALESCE(d.name, 'UNASSIGNED'),
                   COUNT(DISTINCT e.id),
                   SUM(p.netSalary),
                   COUNT(p)
            FROM Payroll p
            JOIN p.employee e
            LEFT JOIN e.department d
            WHERE UPPER(p.status) = 'PAID'
              AND (:month IS NULL OR p.payMonth = :month)
              AND (:year IS NULL OR p.payYear = :year)
            GROUP BY d.id, d.name
            ORDER BY COALESCE(d.name, 'UNASSIGNED')
            """)
    List<Object[]> paidPayrollByDepartment(@Param("month") Integer month,
                                          @Param("year") Integer year);
}

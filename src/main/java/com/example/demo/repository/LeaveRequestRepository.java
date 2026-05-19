package com.example.demo.repository;

import com.example.demo.model.LeaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    Page<LeaveRequest> findByEmployeeId(Long employeeId, Pageable pageable);
    Page<LeaveRequest> findByStatus(String status, Pageable pageable);
    Page<LeaveRequest> findByLeaveType(String leaveType, Pageable pageable);
    Page<LeaveRequest> findByEmployeeIdAndStatus(Long employeeId, String status, Pageable pageable);
    long countByStatusIgnoreCase(String status);
    @Query("""
            SELECT l FROM LeaveRequest l
            WHERE (:employeeId IS NULL OR l.employee.id = :employeeId)
              AND (:status IS NULL OR UPPER(l.status) = UPPER(:status))
              AND (:leaveType IS NULL OR UPPER(l.leaveType) = UPPER(:leaveType))
              AND (:dateFrom IS NULL OR l.startDate >= :dateFrom)
              AND (:dateTo IS NULL OR l.endDate <= :dateTo)
              AND (:keyword IS NULL OR LOWER(l.reason) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(COALESCE(l.remarks, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<LeaveRequest> search(@Param("employeeId") Long employeeId,
                              @Param("status") String status,
                              @Param("leaveType") String leaveType,
                              @Param("dateFrom") LocalDate dateFrom,
                              @Param("dateTo") LocalDate dateTo,
                              @Param("keyword") String keyword,
                              Pageable pageable);

    @Query("""
            SELECT COALESCE(SUM(l.totalDays), 0) FROM LeaveRequest l
            WHERE l.employee.id = :employeeId
              AND UPPER(l.status) = 'APPROVED'
              AND l.startDate >= :startDate
              AND l.endDate <= :endDate
            """)
    Integer sumApprovedDaysByEmployeeAndDateRange(@Param("employeeId") Long employeeId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);
}

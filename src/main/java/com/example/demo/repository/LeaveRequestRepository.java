package com.example.demo.repository;

import com.example.demo.model.LeaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    Page<LeaveRequest> findByEmployeeId(Long employeeId, Pageable pageable);
    Page<LeaveRequest> findByStatus(String status, Pageable pageable);
    Page<LeaveRequest> findByLeaveType(String leaveType, Pageable pageable);
    Page<LeaveRequest> findByEmployeeIdAndStatus(Long employeeId, String status, Pageable pageable);
}

package com.example.demo.repository;

import com.example.demo.model.LeaveApprovalStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveApprovalStepRepository extends JpaRepository<LeaveApprovalStep, Long> {
    List<LeaveApprovalStep> findByLeaveRequestIdOrderByStepOrderAsc(Long leaveRequestId);
    List<LeaveApprovalStep> findByApproverIdAndStatus(Long approverId, String status);
}

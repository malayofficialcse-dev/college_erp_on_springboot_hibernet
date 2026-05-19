package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Employee;
import com.example.demo.model.EmployeeAttendance;
import com.example.demo.model.EmployeeDocument;
import com.example.demo.model.LeaveApprovalStep;
import com.example.demo.model.LeaveRequest;
import com.example.demo.repository.EmployeeAttendanceRepository;
import com.example.demo.repository.EmployeeDocumentRepository;
import com.example.demo.repository.LeaveApprovalStepRepository;
import com.example.demo.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HrFeatureService {

    @Autowired private EmployeeService employeeService;
    @Autowired private EmployeeAttendanceRepository employeeAttendanceRepository;
    @Autowired private EmployeeDocumentRepository employeeDocumentRepository;
    @Autowired private LeaveApprovalStepRepository leaveApprovalStepRepository;
    @Autowired private LeaveRequestRepository leaveRequestRepository;

    public Page<EmployeeAttendance> getAttendance(Pageable pageable) {
        return employeeAttendanceRepository.findAll(pageable);
    }

    public Page<EmployeeAttendance> getAttendanceByEmployee(Long employeeId, Pageable pageable) {
        return employeeAttendanceRepository.findByEmployeeId(employeeId, pageable);
    }

    @Transactional
    public EmployeeAttendance markAttendance(EmployeeAttendance attendance) {
        if (attendance.getAttendanceDate() == null) {
            attendance.setAttendanceDate(LocalDate.now());
        }
        employeeAttendanceRepository.findByEmployeeIdAndAttendanceDate(attendance.getEmployee().getId(), attendance.getAttendanceDate())
                .ifPresent(existing -> attendance.setId(existing.getId()));
        return employeeAttendanceRepository.save(attendance);
    }

    public List<EmployeeDocument> getDocuments(Long employeeId) {
        return employeeDocumentRepository.findByEmployeeId(employeeId, Pageable.unpaged()).getContent();
    }

    @Transactional
    public EmployeeDocument saveDocument(EmployeeDocument document) {
        return employeeDocumentRepository.save(document);
    }

    @Transactional
    public LeaveRequest setupApprovalWorkflow(Long leaveRequestId, List<Long> approverIds) {
        LeaveRequest request = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveRequest", "id", leaveRequestId));
        int order = 1;
        for (Long approverId : approverIds) {
            Employee approver = employeeService.getEmployeeById(approverId);
            LeaveApprovalStep step = new LeaveApprovalStep();
            step.setLeaveRequest(request);
            step.setApprover(approver);
            step.setStepOrder(order++);
            step.setStatus("PENDING");
            leaveApprovalStepRepository.save(step);
        }
        request.setStatus("PENDING_APPROVAL");
        return leaveRequestRepository.save(request);
    }

    public List<LeaveApprovalStep> getApprovalSteps(Long leaveRequestId) {
        return leaveApprovalStepRepository.findByLeaveRequestIdOrderByStepOrderAsc(leaveRequestId);
    }

    @Transactional
    public LeaveRequest actionApprovalStep(Long stepId, String status, String remarks) {
        LeaveApprovalStep step = leaveApprovalStepRepository.findById(stepId)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveApprovalStep", "id", stepId));
        step.setStatus(status);
        step.setRemarks(remarks);
        step.setActionTime(LocalDateTime.now());
        leaveApprovalStepRepository.save(step);

        LeaveRequest request = step.getLeaveRequest();
        List<LeaveApprovalStep> steps = leaveApprovalStepRepository.findByLeaveRequestIdOrderByStepOrderAsc(request.getId());
        boolean anyRejected = steps.stream().anyMatch(item -> "REJECTED".equalsIgnoreCase(item.getStatus()));
        boolean allApproved = steps.stream().allMatch(item -> "APPROVED".equalsIgnoreCase(item.getStatus()));
        if (anyRejected) {
            request.setStatus("REJECTED");
        } else if (allApproved) {
            request.setStatus("APPROVED");
            request.setApprovedBy(step.getApprover());
        } else {
            request.setStatus("IN_REVIEW");
        }
        return leaveRequestRepository.save(request);
    }
}

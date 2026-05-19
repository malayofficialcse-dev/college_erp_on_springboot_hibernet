package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.LeaveRequest;
import com.example.demo.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class LeaveRequestService {

    @Autowired private LeaveRequestRepository leaveRequestRepository;

    public Page<LeaveRequest> getAllLeaveRequests(Pageable pageable) {
        return leaveRequestRepository.findAll(pageable);
    }

    public LeaveRequest getById(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LeaveRequest", "id", id));
    }

    public Page<LeaveRequest> getByEmployee(Long employeeId, Pageable pageable) {
        return leaveRequestRepository.findByEmployeeId(employeeId, pageable);
    }

    public Page<LeaveRequest> getByStatus(String status, Pageable pageable) {
        return leaveRequestRepository.findByStatus(status, pageable);
    }

    @Transactional
    public LeaveRequest applyLeave(LeaveRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }
        int days = (int) ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
        request.setTotalDays(days);
        request.setAppliedDate(LocalDate.now());
        request.setStatus("PENDING");
        return leaveRequestRepository.save(request);
    }

    @Transactional
    public LeaveRequest updateStatus(Long id, String status, String remarks) {
        LeaveRequest request = getById(id);
        request.setStatus(status);
        request.setRemarks(remarks);
        return leaveRequestRepository.save(request);
    }

    @Transactional
    public void delete(Long id) {
        getById(id);
        leaveRequestRepository.deleteById(id);
    }
}

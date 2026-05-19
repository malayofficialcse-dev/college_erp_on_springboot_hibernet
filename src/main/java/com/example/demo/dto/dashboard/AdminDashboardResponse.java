package com.example.demo.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AdminDashboardResponse(
        long totalStudents,
        long totalStaff,
        long totalDepartments,
        long overdueBooks,
        long pendingLeaveRequests,
        BigDecimal totalFeeCollected,
        BigDecimal currentMonthFeeCollected,
        LocalDate reportDate
) {}

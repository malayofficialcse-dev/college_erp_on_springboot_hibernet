package com.example.demo.dto.reports;

import java.time.LocalDate;
import java.util.List;

public record DepartmentPerformanceSummaryResponse(
        LocalDate fromDate,
        LocalDate toDate,
        Long examId,
        Long courseId,
        List<DepartmentPerformanceItem> departments
) {
    public record DepartmentPerformanceItem(
            Long departmentId,
            String departmentName,
            long studentCount,
            Double averageGradePoint,
            Double passRatePercentage,
            Double attendanceRatePercentage
    ) {}
}


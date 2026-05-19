package com.example.demo.dto.reports;

import java.math.BigDecimal;
import java.util.List;

public record PayrollByDepartmentReportResponse(
        Integer month,
        Integer year,
        BigDecimal totalNetPaid,
        List<DepartmentPayrollItem> departments
) {
    public record DepartmentPayrollItem(
            Long departmentId,
            String departmentName,
            long employeeCount,
            BigDecimal totalNetSalary,
            long payrollCount
    ) {}
}


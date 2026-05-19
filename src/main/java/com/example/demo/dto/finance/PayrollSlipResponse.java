package com.example.demo.dto.finance;

import java.math.BigDecimal;

public record PayrollSlipResponse(
        Long payrollId,
        String employeeCode,
        String employeeName,
        String designation,
        Integer payMonth,
        Integer payYear,
        BigDecimal basicSalary,
        BigDecimal hra,
        BigDecimal da,
        BigDecimal ta,
        BigDecimal otherAllowances,
        BigDecimal bonus,
        BigDecimal grossSalary,
        BigDecimal pfDeduction,
        BigDecimal esiDeduction,
        BigDecimal taxDeduction,
        BigDecimal otherDeductions,
        BigDecimal netSalary,
        String status
) {}

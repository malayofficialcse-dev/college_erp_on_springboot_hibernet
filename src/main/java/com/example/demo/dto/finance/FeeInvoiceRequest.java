package com.example.demo.dto.finance;

import java.time.LocalDate;

public record FeeInvoiceRequest(
        Long studentId,
        Long feeStructureId,
        LocalDate dueDate,
        String remarks
) {}

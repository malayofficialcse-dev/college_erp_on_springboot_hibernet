package com.example.demo.dto.reports;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record LibraryOverdueReportResponse(
        LocalDate asOfDate,
        long overdueCount,
        BigDecimal totalFineAmount,
        List<OverdueItem> overdueItems
) {
    public record OverdueItem(
            Long issueId,
            Long bookId,
            String bookTitle,
            String borrowerType,
            Long borrowerId,
            String borrowerName,
            LocalDate issueDate,
            LocalDate dueDate,
            long daysOverdue,
            BigDecimal fineAmount,
            String status
    ) {}
}


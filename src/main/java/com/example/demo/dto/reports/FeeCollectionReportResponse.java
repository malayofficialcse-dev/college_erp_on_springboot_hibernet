package com.example.demo.dto.reports;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record FeeCollectionReportResponse(
        LocalDate fromDate,
        LocalDate toDate,
        BigDecimal totalCollected,
        List<MonthlyCollectionItem> months
) {
    public record MonthlyCollectionItem(
            int year,
            int month,
            BigDecimal collectedAmount,
            long paymentCount
    ) {}
}


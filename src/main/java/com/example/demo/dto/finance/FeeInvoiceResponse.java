package com.example.demo.dto.finance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record FeeInvoiceResponse(
        Long id,
        String invoiceNumber,
        Long studentId,
        String studentName,
        String semester,
        BigDecimal baseAmount,
        BigDecimal scholarshipAdjustment,
        BigDecimal lateFee,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        BigDecimal outstandingAmount,
        LocalDate dueDate,
        String status,
        List<PaymentHistoryItem> payments
) {
    public record PaymentHistoryItem(
            Long id,
            BigDecimal amount,
            LocalDate paymentDate,
            String paymentMethod,
            String referenceNumber,
            String status
    ) {}
}

package com.example.demo.dto.finance;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FeePaymentTransactionRequest(
        BigDecimal amount,
        LocalDate paymentDate,
        String paymentMethod,
        String referenceNumber,
        String remarks
) {}

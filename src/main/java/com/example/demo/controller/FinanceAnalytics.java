package com.example.demo.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinanceAnalytics {
    private double totalFeeCollected;
    private double totalFeeDue;
    private double collectionRate;
    private int totalStudents;
    private Map<String, Double> departmentWiseCollection;
    private List<MonthlyTrend> monthlyTrends;
    private List<PaymentStatus> paymentStatus;
    private Map<String, Double> feeBreakdown;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyTrend {
        private String month;
        private double collected;
        private double due;
        private int transactions;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentStatus {
        private String status;
        private int count;
        private double amount;
        private double percentage;
    }
}

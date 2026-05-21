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
public class StudentPerformanceAnalytics {
    private double averageGPA;
    private int totalStudents;
    private int passedStudents;
    private int failedStudents;
    private double passRate;
    private Map<String, Integer> gradeDistribution; // A, B, C, D, F
    private List<SubjectPerformance> subjectPerformance;
    private List<DepartmentPerformance> departmentWise;
    private TrendData gpatrends;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubjectPerformance {
        private String subjectName;
        private double averageScore;
        private int studentCount;
        private double passRate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DepartmentPerformance {
        private String departmentName;
        private double averageGPA;
        private int studentCount;
        private double passRate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TrendData {
        private List<String> months;
        private List<Double> averageGPAs;
        private List<Integer> passCounts;
        private List<Integer> failCounts;
    }
}

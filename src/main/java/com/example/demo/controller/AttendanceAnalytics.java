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
public class AttendanceAnalytics {
    private double averageAttendanceRate;
    private int totalStudents;
    private Map<String, Double> departmentWiseAttendance;
    private List<AttendanceTrend> monthlyTrends;
    private List<StudentAttendanceSummary> absenteeReport;
    private Map<String, Integer> attendanceDistribution;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttendanceTrend {
        private String month;
        private double attendanceRate;
        private int presentCount;
        private int absentCount;
        private int totalClasses;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudentAttendanceSummary {
        private String studentName;
        private String rollNumber;
        private String department;
        private double attendancePercentage;
        private int presentDays;
        private int absentDays;
        private int totalDays;
    }
}

package com.example.demo.dto.academic;

import java.util.List;

public record AttendanceAnalyticsResponse(
        Long studentId,
        String studentName,
        double overallPercentage,
        List<SubjectAttendanceAnalytics> bySubject,
        List<MonthlyAttendanceTrend> monthlyTrends,
        List<ShortageItem> shortages
) {
    public record SubjectAttendanceAnalytics(
            Long subjectId,
            String subjectName,
            long attended,
            long total,
            double percentage
    ) {}

    public record MonthlyAttendanceTrend(
            String month,
            long attended,
            long total,
            double percentage
    ) {}

    public record ShortageItem(
            Long subjectId,
            String subjectName,
            double percentage,
            double requiredThreshold
    ) {}
}

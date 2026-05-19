package com.example.demo.dto.dashboard;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record StudentDashboardResponse(
        StudentProfile profile,
        AttendanceSummary attendanceSummary,
        FeeSummary feeStatus,
        List<ResultSummary> recentResults,
        List<NoticeSummary> notices
) {
    public record StudentProfile(
            Long id,
            String enrollmentNumber,
            String fullName,
            String email,
            String phone,
            String status,
            Integer currentSemester,
            String department,
            List<String> courses
    ) {}

    public record AttendanceSummary(
            long totalClasses,
            long classesAttended,
            double attendancePercentage,
            List<SubjectAttendance> subjectBreakdown
    ) {}

    public record SubjectAttendance(
            Long subjectId,
            String subjectName,
            long totalClasses,
            long classesAttended,
            double attendancePercentage
    ) {}

    public record FeeSummary(
            BigDecimal totalPaid,
            long pendingPayments,
            long overduePayments,
            List<PaymentSummary> recentPayments
    ) {}

    public record PaymentSummary(
            Long id,
            BigDecimal netAmount,
            String status,
            String semester,
            LocalDate paymentDate,
            String receiptNumber
    ) {}

    public record ResultSummary(
            Long id,
            String examName,
            String subjectName,
            Double marksObtained,
            Double totalMarks,
            String grade,
            String resultStatus,
            String semester
    ) {}

    public record NoticeSummary(
            Long id,
            String title,
            String noticeType,
            String targetAudience,
            String department,
            LocalDateTime publishedAt,
            LocalDate expiryDate
    ) {}
}

package com.example.demo.dto.reports;

import java.time.LocalDate;
import java.util.List;

public record AttendanceReportResponse(
        Long studentId,
        LocalDate fromDate,
        LocalDate toDate,
        List<SubjectAttendanceItem> subjects
) {
    public record SubjectAttendanceItem(
            Long subjectId,
            String subjectCode,
            String subjectName,
            long totalClasses,
            long presentClasses,
            long absentClasses,
            double attendancePercentage
    ) {}
}


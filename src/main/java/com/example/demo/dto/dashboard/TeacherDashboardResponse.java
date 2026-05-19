package com.example.demo.dto.dashboard;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record TeacherDashboardResponse(
        TeacherProfile profile,
        List<SubjectSummary> assignedSubjects,
        List<ClassSummary> classesToday,
        int pendingAttendanceCount,
        LeaveBalance leaveBalance,
        List<ClassSummary> timetable
) {
    public record TeacherProfile(
            Long id,
            String employeeCode,
            String fullName,
            String email,
            String designation,
            String qualification,
            String department,
            String status
    ) {}

    public record SubjectSummary(
            Long id,
            String subjectCode,
            String name,
            Integer semesterNumber,
            String subjectType,
            String courseTitle
    ) {}

    public record ClassSummary(
            Long timetableId,
            String dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            String roomNumber,
            String classType,
            String subjectName,
            String courseTitle,
            String semesterName
    ) {}

    public record LeaveBalance(
            int annualLimit,
            int approvedDaysUsed,
            int remainingDays,
            LocalDate periodStart,
            LocalDate periodEnd
    ) {}
}

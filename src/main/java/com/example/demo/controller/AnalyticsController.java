package com.example.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "Advanced analytics and insights for all modules")
@PreAuthorize("isAuthenticated()")
public class AnalyticsController {

    @Autowired
    private StudentAnalyticsService studentAnalyticsService;

    @Autowired
    private FinanceAnalyticsService financeAnalyticsService;

    @Autowired
    private AttendanceAnalyticsService attendanceAnalyticsService;

    @GetMapping("/student-performance")
    @Operation(summary = "Get student performance analytics", description = "Returns GPA trends, pass rates, subject performance")
    public ResponseEntity<StudentPerformanceAnalytics> getStudentPerformanceAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String department) {
        
        if (startDate == null) startDate = LocalDate.now().minusMonths(6);
        if (endDate == null) endDate = LocalDate.now();

        StudentPerformanceAnalytics analytics = studentAnalyticsService
            .getStudentPerformanceAnalytics(startDate, endDate, department);
        
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/finance")
    @Operation(summary = "Get finance analytics", description = "Returns fee collection trends, pending fees, department analysis")
    public ResponseEntity<FinanceAnalytics> getFinanceAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String department) {
        
        if (startDate == null) startDate = LocalDate.now().minusMonths(6);
        if (endDate == null) endDate = LocalDate.now();

        FinanceAnalytics analytics = financeAnalyticsService
            .getFinanceAnalytics(startDate, endDate, department);
        
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/attendance")
    @Operation(summary = "Get attendance analytics", description = "Returns attendance patterns, absentee rates, comparisons")
    public ResponseEntity<AttendanceAnalytics> getAttendanceAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String department) {
        
        if (startDate == null) startDate = LocalDate.now().minusMonths(6);
        if (endDate == null) endDate = LocalDate.now();

        AttendanceAnalytics analytics = attendanceAnalyticsService
            .getAttendanceAnalytics(startDate, endDate, department);
        
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/student-performance/export")
    @Operation(summary = "Export student performance analytics", description = "Export analytics as PDF, Excel, or CSV")
    public ResponseEntity<byte[]> exportStudentPerformance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "PDF") String format,
            @RequestParam(required = false) String department) throws Exception {
        
        if (startDate == null) startDate = LocalDate.now().minusMonths(6);
        if (endDate == null) endDate = LocalDate.now();

        StudentPerformanceAnalytics analytics = studentAnalyticsService
            .getStudentPerformanceAnalytics(startDate, endDate, department);

        // Convert to export format (PDF/Excel/CSV)
        byte[] content = convertAnalyticsToFormat(analytics, format);

        String fileName = "Student-Performance-Analytics." + format.toLowerCase();
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=" + fileName)
            .header("Content-Type", getContentType(format))
            .body(content);
    }

    @GetMapping("/finance/export")
    @Operation(summary = "Export finance analytics", description = "Export analytics as PDF, Excel, or CSV")
    public ResponseEntity<byte[]> exportFinanceAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "PDF") String format,
            @RequestParam(required = false) String department) throws Exception {
        
        if (startDate == null) startDate = LocalDate.now().minusMonths(6);
        if (endDate == null) endDate = LocalDate.now();

        FinanceAnalytics analytics = financeAnalyticsService
            .getFinanceAnalytics(startDate, endDate, department);

        byte[] content = convertAnalyticsToFormat(analytics, format);

        String fileName = "Finance-Analytics." + format.toLowerCase();
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=" + fileName)
            .header("Content-Type", getContentType(format))
            .body(content);
    }

    @GetMapping("/attendance/export")
    @Operation(summary = "Export attendance analytics", description = "Export analytics as PDF, Excel, or CSV")
    public ResponseEntity<byte[]> exportAttendanceAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "PDF") String format,
            @RequestParam(required = false) String department) throws Exception {
        
        if (startDate == null) startDate = LocalDate.now().minusMonths(6);
        if (endDate == null) endDate = LocalDate.now();

        AttendanceAnalytics analytics = attendanceAnalyticsService
            .getAttendanceAnalytics(startDate, endDate, department);

        byte[] content = convertAnalyticsToFormat(analytics, format);

        String fileName = "Attendance-Analytics." + format.toLowerCase();
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=" + fileName)
            .header("Content-Type", getContentType(format))
            .body(content);
    }

    private byte[] convertAnalyticsToFormat(Object analytics, String format) throws Exception {
        // Placeholder implementation - would convert analytics object to PDF/Excel/CSV
        return "Analytics Export Data".getBytes();
    }

    private String getContentType(String format) {
        switch (format.toUpperCase()) {
            case "PDF":
                return "application/pdf";
            case "EXCEL":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "CSV":
                return "text/csv";
            default:
                return "application/octet-stream";
        }
    }
}

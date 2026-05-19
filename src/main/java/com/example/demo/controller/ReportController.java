package com.example.demo.controller;

import com.example.demo.dto.reports.AttendanceReportResponse;
import com.example.demo.dto.reports.DepartmentPerformanceSummaryResponse;
import com.example.demo.dto.reports.FeeCollectionReportResponse;
import com.example.demo.dto.reports.LibraryOverdueReportResponse;
import com.example.demo.dto.reports.PayrollByDepartmentReportResponse;
import com.example.demo.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired private ReportService reportService;

    @GetMapping("/attendance")
    public ResponseEntity<AttendanceReportResponse> attendanceReport(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate toDate
    ) {
        return ResponseEntity.ok(reportService.attendanceByStudentAndSubject(studentId, subjectId, fromDate, toDate));
    }

    @GetMapping("/fees/collection")
    public ResponseEntity<FeeCollectionReportResponse> feeCollectionByMonth(
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate toDate
    ) {
        return ResponseEntity.ok(reportService.feeCollectionByMonth(fromDate, toDate));
    }

    @GetMapping("/payroll/by-department")
    public ResponseEntity<PayrollByDepartmentReportResponse> payrollByDepartment(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        return ResponseEntity.ok(reportService.payrollPaidByDepartment(month, year));
    }

    @GetMapping("/library/overdue")
    public ResponseEntity<LibraryOverdueReportResponse> libraryOverdue(
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate asOfDate
    ) {
        return ResponseEntity.ok(reportService.libraryOverdue(asOfDate));
    }

    @GetMapping("/departments/performance")
    public ResponseEntity<DepartmentPerformanceSummaryResponse> departmentPerformance(
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) Long courseId
    ) {
        return ResponseEntity.ok(reportService.departmentPerformance(fromDate, toDate, examId, courseId));
    }
}


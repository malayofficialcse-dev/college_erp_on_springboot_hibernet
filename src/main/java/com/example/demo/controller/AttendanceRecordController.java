package com.example.demo.controller;

import com.example.demo.model.AttendanceRecord;
import com.example.demo.service.AttendanceRecordService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceRecordController {

    @Autowired private AttendanceRecordService attendanceService;
    @Autowired private ExportService exportService;

    @GetMapping
    public ResponseEntity<Page<AttendanceRecord>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(attendanceService.getAllRecords(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceRecord> getById(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceService.getById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<Page<AttendanceRecord>> getByStudent(@PathVariable Long studentId,
                                                               @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(attendanceService.getByStudent(studentId, pageable));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<Page<AttendanceRecord>> getByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(attendanceService.getByDate(date, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AttendanceRecord>> search(@RequestParam(required = false) Long studentId,
                                                         @RequestParam(required = false) Long subjectId,
                                                         @RequestParam(required = false) Long teacherId,
                                                         @RequestParam(required = false) String status,
                                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
                                                         @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(attendanceService.search(studentId, subjectId, teacherId, status, dateFrom, dateTo, pageable));
    }

    @GetMapping("/percentage")
    public ResponseEntity<Map<String, Object>> getAttendancePercentage(@RequestParam Long studentId,
                                                                        @RequestParam Long subjectId) {
        return ResponseEntity.ok(attendanceService.getAttendancePercentage(studentId, subjectId));
    }

    @PostMapping
    public ResponseEntity<AttendanceRecord> create(@RequestBody AttendanceRecord record) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.create(record));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceRecord> update(@PathVariable Long id,
                                                   @RequestBody AttendanceRecord details) {
        return ResponseEntity.ok(attendanceService.update(id, details));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        attendanceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export/all")
    @Operation(summary = "Export attendance records", description = "Export all attendance records as PDF, Excel, or CSV with filters")
    public ResponseEntity<byte[]> exportAttendanceRecords(
            @RequestParam(defaultValue = "PDF") String format,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) throws Exception {
        
        List<AttendanceRecord> records = attendanceService.getAllRecords().stream().collect(Collectors.toList());
        
        if (studentId != null) {
            records = records.stream()
                .filter(r -> r.getStudent().getId().equals(studentId))
                .collect(Collectors.toList());
        }
        
        if (dateFrom != null && dateTo != null) {
            records = records.stream()
                .filter(r -> !r.getDate().isBefore(dateFrom) && !r.getDate().isAfter(dateTo))
                .collect(Collectors.toList());
        }

        List<String> headers = Arrays.asList(
            "ID", "Student", "Subject", "Date", "Present", "Remarks"
        );

        List<Map<String, Object>> data = records.stream()
            .map(r -> {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("ID", r.getId());
                row.put("Student", r.getStudent().getUser().getFullName());
                row.put("Subject", r.getSubject().getName());
                row.put("Date", r.getDate());
                row.put("Present", r.isPresent() ? "Yes" : "No");
                row.put("Remarks", r.getRemarks() != null ? r.getRemarks() : "-");
                return row;
            })
            .collect(Collectors.toList());

        ExportRequest request = ExportRequest.builder()
            .format(format)
            .module("Attendance")
            .title("Attendance Records Export")
            .includeSummary(true)
            .build();

        byte[] content = exportService.generateExport(request, headers, data);

        String fileName = "Attendance-Export." + format.toLowerCase();
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=" + fileName)
            .header("Content-Type", getContentType(format))
            .body(content);
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

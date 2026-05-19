package com.example.demo.controller;

import com.example.demo.model.AttendanceRecord;
import com.example.demo.service.AttendanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceRecordController {

    @Autowired private AttendanceRecordService attendanceService;

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
}

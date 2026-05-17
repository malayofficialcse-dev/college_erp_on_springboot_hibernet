package com.example.demo.controller;

import com.example.demo.model.AttendanceRecord;
import com.example.demo.service.AttendanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceRecordController {

    @Autowired
    private AttendanceRecordService attendanceRecordService;

    @GetMapping
    public List<AttendanceRecord> getAllAttendanceRecords() {
        return attendanceRecordService.getAllAttendanceRecords();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceRecord> getAttendanceRecordById(@PathVariable Long id) {
        AttendanceRecord attendanceRecord = attendanceRecordService.getAttendanceRecordById(id);
        if (attendanceRecord != null) {
            return ResponseEntity.ok(attendanceRecord);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public AttendanceRecord createAttendanceRecord(@RequestBody AttendanceRecord attendanceRecord) {
        return attendanceRecordService.saveAttendanceRecord(attendanceRecord);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceRecord> updateAttendanceRecord(@PathVariable Long id, @RequestBody AttendanceRecord attendanceRecordDetails) {
        AttendanceRecord updatedAttendanceRecord = attendanceRecordService.updateAttendanceRecord(id, attendanceRecordDetails);
        if (updatedAttendanceRecord != null) {
            return ResponseEntity.ok(updatedAttendanceRecord);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendanceRecord(@PathVariable Long id) {
        attendanceRecordService.deleteAttendanceRecord(id);
        return ResponseEntity.ok().build();
    }
}

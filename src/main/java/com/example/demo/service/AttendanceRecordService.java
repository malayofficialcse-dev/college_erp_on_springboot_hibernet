package com.example.demo.service;

import com.example.demo.model.AttendanceRecord;
import com.example.demo.repository.AttendanceRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AttendanceRecordService {

    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;

    public List<AttendanceRecord> getAllAttendanceRecords() {
        return attendanceRecordRepository.findAll();
    }

    public AttendanceRecord getAttendanceRecordById(Long id) {
        return attendanceRecordRepository.findById(id).orElse(null);
    }

    public AttendanceRecord saveAttendanceRecord(AttendanceRecord attendanceRecord) {
        return attendanceRecordRepository.save(attendanceRecord);
    }

    public AttendanceRecord updateAttendanceRecord(Long id, AttendanceRecord attendanceRecordDetails) {
        Optional<AttendanceRecord> attendanceRecord = attendanceRecordRepository.findById(id);
        if (attendanceRecord.isPresent()) {
            AttendanceRecord existing = attendanceRecord.get();
            existing.setStudent(attendanceRecordDetails.getStudent());
            existing.setSubject(attendanceRecordDetails.getSubject());
            existing.setDate(attendanceRecordDetails.getDate());
            existing.setStatus(attendanceRecordDetails.getStatus());
            return attendanceRecordRepository.save(existing);
        }
        return null;
    }

    public void deleteAttendanceRecord(Long id) {
        attendanceRecordRepository.deleteById(id);
    }
}

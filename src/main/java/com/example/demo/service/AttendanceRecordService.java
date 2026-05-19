package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.AttendanceRecord;
import com.example.demo.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

@Service
public class AttendanceRecordService {

    @Autowired private AttendanceRepository attendanceRepository;

    public Page<AttendanceRecord> getAllRecords(Pageable pageable) {
        return attendanceRepository.findAll(pageable);
    }

    public AttendanceRecord getById(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AttendanceRecord", "id", id));
    }

    public Page<AttendanceRecord> getByStudent(Long studentId, Pageable pageable) {
        return attendanceRepository.findByStudentId(studentId, pageable);
    }

    public Page<AttendanceRecord> getBySubject(Long subjectId, Pageable pageable) {
        return attendanceRepository.findBySubjectId(subjectId, pageable);
    }

    public Page<AttendanceRecord> getByDate(LocalDate date, Pageable pageable) {
        return attendanceRepository.findByDate(date, pageable);
    }

    public Page<AttendanceRecord> search(Long studentId, Long subjectId, Long teacherId,
                                         String status, LocalDate dateFrom, LocalDate dateTo,
                                         Pageable pageable) {
        return attendanceRepository.search(studentId, subjectId, teacherId, emptyToNull(status), dateFrom, dateTo, pageable);
    }

    public Map<String, Object> getAttendancePercentage(Long studentId, Long subjectId) {
        long present = attendanceRepository.countPresentByStudentAndSubject(studentId, subjectId);
        long total = attendanceRepository.countTotalByStudentAndSubject(studentId, subjectId);
        double percentage = total > 0 ? (present * 100.0 / total) : 0.0;
        return Map.of(
            "studentId", studentId,
            "subjectId", subjectId,
            "totalClasses", total,
            "classesAttended", present,
            "percentageAttendance", String.format("%.2f", percentage)
        );
    }

    @Transactional
    public AttendanceRecord create(AttendanceRecord record) {
        return attendanceRepository.save(record);
    }

    @Transactional
    public AttendanceRecord update(Long id, AttendanceRecord details) {
        AttendanceRecord record = getById(id);
        record.setStatus(details.getStatus());
        record.setRemarks(details.getRemarks());
        return attendanceRepository.save(record);
    }

    @Transactional
    public void delete(Long id) {
        getById(id);
        attendanceRepository.deleteById(id);
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }
}

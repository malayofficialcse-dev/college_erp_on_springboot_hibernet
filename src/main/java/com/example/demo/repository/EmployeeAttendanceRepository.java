package com.example.demo.repository;

import com.example.demo.model.EmployeeAttendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeAttendanceRepository extends JpaRepository<EmployeeAttendance, Long> {
    Page<EmployeeAttendance> findByEmployeeId(Long employeeId, Pageable pageable);
    Page<EmployeeAttendance> findByAttendanceDate(LocalDate attendanceDate, Pageable pageable);
    List<EmployeeAttendance> findByEmployeeIdAndAttendanceDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);
    Optional<EmployeeAttendance> findByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate attendanceDate);
}

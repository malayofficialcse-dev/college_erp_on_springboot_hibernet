package com.example.demo.controller;

import com.example.demo.model.AttendanceRecord;
import com.example.demo.service.AttendanceRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AttendanceAnalyticsService {

    @Autowired
    private AttendanceRecordService attendanceService;

    public AttendanceAnalytics getAttendanceAnalytics(LocalDate startDate, LocalDate endDate, String department) {
        log.info("Fetching attendance analytics from {} to {}", startDate, endDate);

        try {
            List<AttendanceRecord> records = attendanceService.getAllAttendanceRecords();
            
            if (records.isEmpty()) {
                return AttendanceAnalytics.builder()
                    .averageAttendanceRate(0.0)
                    .totalStudents(0)
                    .build();
            }

            // Calculate average attendance rate
            double totalAttendance = records.stream()
                .mapToDouble(r -> r.isPresent() ? 1.0 : 0.0)
                .sum();
            double averageAttendanceRate = (totalAttendance / records.size()) * 100;

            // Department-wise attendance
            Map<String, Double> deptWiseAttendance = calculateDepartmentWiseAttendance(records);

            // Monthly trends
            List<AttendanceAnalytics.AttendanceTrend> monthlyTrends = getMonthlyTrends(records, startDate, endDate);

            // Absentee report (students with low attendance)
            List<AttendanceAnalytics.StudentAttendanceSummary> absenteeReport = getAbsenteeReport(records);

            // Attendance distribution
            Map<String, Integer> distribution = new LinkedHashMap<>();
            distribution.put("90-100%", (int) absenteeReport.stream().filter(a -> a.getAttendancePercentage() >= 90).count());
            distribution.put("75-89%", (int) absenteeReport.stream().filter(a -> a.getAttendancePercentage() >= 75 && a.getAttendancePercentage() < 90).count());
            distribution.put("60-74%", (int) absenteeReport.stream().filter(a -> a.getAttendancePercentage() >= 60 && a.getAttendancePercentage() < 75).count());
            distribution.put("Below 60%", (int) absenteeReport.stream().filter(a -> a.getAttendancePercentage() < 60).count());

            return AttendanceAnalytics.builder()
                .averageAttendanceRate(Math.round(averageAttendanceRate * 100.0) / 100.0)
                .totalStudents(records.stream().map(r -> r.getStudent().getId()).distinct().count() > 0 ? 
                    (int) records.stream().map(r -> r.getStudent().getId()).distinct().count() : 0)
                .departmentWiseAttendance(deptWiseAttendance)
                .monthlyTrends(monthlyTrends)
                .absenteeReport(absenteeReport)
                .attendanceDistribution(distribution)
                .build();
        } catch (Exception e) {
            log.error("Error fetching attendance analytics", e);
            return AttendanceAnalytics.builder().build();
        }
    }

    private Map<String, Double> calculateDepartmentWiseAttendance(List<AttendanceRecord> records) {
        return records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getStudent().getDepartment().getName(),
                Collectors.averagingDouble(r -> r.isPresent() ? 1.0 : 0.0)
            ))
            .entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> Math.round(e.getValue() * 100 * 100.0) / 100.0
            ));
    }

    private List<AttendanceAnalytics.AttendanceTrend> getMonthlyTrends(List<AttendanceRecord> records, LocalDate startDate, LocalDate endDate) {
        List<AttendanceAnalytics.AttendanceTrend> trends = new ArrayList<>();
        YearMonth current = YearMonth.from(startDate);
        YearMonth end = YearMonth.from(endDate);

        while (!current.isAfter(end)) {
            final YearMonth month = current;
            List<AttendanceRecord> monthRecords = records.stream()
                .filter(r -> YearMonth.from(r.getDate()).equals(month))
                .collect(Collectors.toList());

            if (!monthRecords.isEmpty()) {
                long presentCount = monthRecords.stream().filter(AttendanceRecord::isPresent).count();
                long absentCount = monthRecords.size() - presentCount;
                double rate = (presentCount * 100.0) / monthRecords.size();

                trends.add(AttendanceAnalytics.AttendanceTrend.builder()
                    .month(month.toString())
                    .attendanceRate(Math.round(rate * 100.0) / 100.0)
                    .presentCount((int) presentCount)
                    .absentCount((int) absentCount)
                    .totalClasses(monthRecords.size())
                    .build());
            }
            current = current.plusMonths(1);
        }

        return trends;
    }

    private List<AttendanceAnalytics.StudentAttendanceSummary> getAbsenteeReport(List<AttendanceRecord> records) {
        return records.stream()
            .collect(Collectors.groupingBy(
                r -> r.getStudent(),
                Collectors.reducing(
                    new int[]{0, 0, 0}, // {present, absent, total}
                    r -> new int[]{r.isPresent() ? 1 : 0, r.isPresent() ? 0 : 1, 1},
                    (a, b) -> new int[]{a[0] + b[0], a[1] + b[1], a[2] + b[2]}
                )
            ))
            .entrySet().stream()
            .map(entry -> {
                int[] counts = entry.getValue().orElse(new int[]{0, 0, 0});
                double percentage = counts[2] > 0 ? (counts[0] * 100.0) / counts[2] : 0;

                return AttendanceAnalytics.StudentAttendanceSummary.builder()
                    .studentName(entry.getKey().getUser().getFullName())
                    .rollNumber(entry.getKey().getRollNumber())
                    .department(entry.getKey().getDepartment().getName())
                    .attendancePercentage(Math.round(percentage * 100.0) / 100.0)
                    .presentDays(counts[0])
                    .absentDays(counts[1])
                    .totalDays(counts[2])
                    .build();
            })
            .sorted(Comparator.comparingDouble(AttendanceAnalytics.StudentAttendanceSummary::getAttendancePercentage))
            .collect(Collectors.toList());
    }
}

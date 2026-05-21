package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StudentAnalyticsService {

    @Autowired
    private StudentService studentService;

    @Autowired
    private ExamResultService examResultService;

    @Autowired
    private AttendanceRecordService attendanceService;

    @Autowired
    private DepartmentService departmentService;

    public StudentPerformanceAnalytics getStudentPerformanceAnalytics(LocalDate startDate, LocalDate endDate, String department) {
        log.info("Fetching student performance analytics from {} to {}", startDate, endDate);

        try {
            // Get all exam results within date range
            List<ExamResult> results = examResultService.getAllExamResults(); // Filter by date in real implementation
            
            if (results.isEmpty()) {
                return StudentPerformanceAnalytics.builder()
                    .averageGPA(0.0)
                    .totalStudents(0)
                    .passedStudents(0)
                    .failedStudents(0)
                    .passRate(0.0)
                    .build();
            }

            // Calculate statistics
            double totalGPA = 0;
            int passCount = 0;
            int failCount = 0;
            Map<String, Integer> gradeDistribution = new HashMap<>();
            gradeDistribution.put("A", 0);
            gradeDistribution.put("B", 0);
            gradeDistribution.put("C", 0);
            gradeDistribution.put("D", 0);
            gradeDistribution.put("F", 0);

            for (ExamResult result : results) {
                double marks = result.getMarksObtained();
                totalGPA += marks;
                String grade = calculateGrade(marks);
                gradeDistribution.put(grade, gradeDistribution.get(grade) + 1);

                if (marks >= 40) passCount++;
                else failCount++;
            }

            double averageGPA = results.isEmpty() ? 0 : totalGPA / results.size();
            double passRate = results.isEmpty() ? 0 : (passCount * 100.0) / results.size();

            // Get subject-wise performance
            List<StudentPerformanceAnalytics.SubjectPerformance> subjectPerformance = 
                getSubjectWisePerformance(results);

            // Get department-wise performance
            List<StudentPerformanceAnalytics.DepartmentPerformance> deptPerformance = 
                getDepartmentWisePerformance(results);

            // Get trends
            StudentPerformanceAnalytics.TrendData trends = getPerformanceTrends(startDate, endDate);

            return StudentPerformanceAnalytics.builder()
                .averageGPA(Math.round(averageGPA * 100.0) / 100.0)
                .totalStudents(results.size())
                .passedStudents(passCount)
                .failedStudents(failCount)
                .passRate(Math.round(passRate * 100.0) / 100.0)
                .gradeDistribution(gradeDistribution)
                .subjectPerformance(subjectPerformance)
                .departmentWise(deptPerformance)
                .gpatrends(trends)
                .build();
        } catch (Exception e) {
            log.error("Error fetching student performance analytics", e);
            return StudentPerformanceAnalytics.builder().build();
        }
    }

    private String calculateGrade(double marks) {
        if (marks >= 90) return "A";
        if (marks >= 80) return "B";
        if (marks >= 70) return "C";
        if (marks >= 60) return "D";
        return "F";
    }

    private List<StudentPerformanceAnalytics.SubjectPerformance> getSubjectWisePerformance(List<ExamResult> results) {
        return results.stream()
            .collect(Collectors.groupingBy(r -> r.getExam().getSubject().getName()))
            .entrySet().stream()
            .map(entry -> {
                double avgScore = entry.getValue().stream()
                    .mapToDouble(ExamResult::getMarksObtained)
                    .average()
                    .orElse(0);
                int passCount = (int) entry.getValue().stream()
                    .filter(r -> r.getMarksObtained() >= 40)
                    .count();
                double passRate = (passCount * 100.0) / entry.getValue().size();

                return StudentPerformanceAnalytics.SubjectPerformance.builder()
                    .subjectName(entry.getKey())
                    .averageScore(Math.round(avgScore * 100.0) / 100.0)
                    .studentCount(entry.getValue().size())
                    .passRate(Math.round(passRate * 100.0) / 100.0)
                    .build();
            })
            .collect(Collectors.toList());
    }

    private List<StudentPerformanceAnalytics.DepartmentPerformance> getDepartmentWisePerformance(List<ExamResult> results) {
        return results.stream()
            .collect(Collectors.groupingBy(r -> r.getStudent().getDepartment().getName()))
            .entrySet().stream()
            .map(entry -> {
                double avgGPA = entry.getValue().stream()
                    .mapToDouble(ExamResult::getMarksObtained)
                    .average()
                    .orElse(0);
                int passCount = (int) entry.getValue().stream()
                    .filter(r -> r.getMarksObtained() >= 40)
                    .count();
                double passRate = (passCount * 100.0) / entry.getValue().size();

                return StudentPerformanceAnalytics.DepartmentPerformance.builder()
                    .departmentName(entry.getKey())
                    .averageGPA(Math.round(avgGPA * 100.0) / 100.0)
                    .studentCount(entry.getValue().size())
                    .passRate(Math.round(passRate * 100.0) / 100.0)
                    .build();
            })
            .collect(Collectors.toList());
    }

    private StudentPerformanceAnalytics.TrendData getPerformanceTrends(LocalDate startDate, LocalDate endDate) {
        List<String> months = new ArrayList<>();
        List<Double> averageGPAs = new ArrayList<>();
        List<Integer> passCounts = new ArrayList<>();
        List<Integer> failCounts = new ArrayList<>();

        YearMonth current = YearMonth.from(startDate);
        YearMonth end = YearMonth.from(endDate);

        while (!current.isAfter(end)) {
            months.add(current.toString());
            averageGPAs.add(75.0); // Placeholder
            passCounts.add(50);
            failCounts.add(10);
            current = current.plusMonths(1);
        }

        return StudentPerformanceAnalytics.TrendData.builder()
            .months(months)
            .averageGPAs(averageGPAs)
            .passCounts(passCounts)
            .failCounts(failCounts)
            .build();
    }
}

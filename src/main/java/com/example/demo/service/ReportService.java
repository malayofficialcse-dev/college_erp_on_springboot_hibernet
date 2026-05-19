package com.example.demo.service;

import com.example.demo.dto.reports.AttendanceReportResponse;
import com.example.demo.dto.reports.DepartmentPerformanceSummaryResponse;
import com.example.demo.dto.reports.FeeCollectionReportResponse;
import com.example.demo.dto.reports.LibraryOverdueReportResponse;
import com.example.demo.dto.reports.PayrollByDepartmentReportResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.BookIssue;
import com.example.demo.model.Student;
import com.example.demo.repository.AttendanceRecordRepository;
import com.example.demo.repository.BookIssueRepository;
import com.example.demo.repository.ExamResultRepository;
import com.example.demo.repository.FeePaymentRepository;
import com.example.demo.repository.PayrollRepository;
import com.example.demo.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired private AttendanceRecordRepository attendanceRecordRepository;
    @Autowired private FeePaymentRepository feePaymentRepository;
    @Autowired private PayrollRepository payrollRepository;
    @Autowired private BookIssueRepository bookIssueRepository;
    @Autowired private ExamResultRepository examResultRepository;
    @Autowired private StudentRepository studentRepository;

    public AttendanceReportResponse attendanceByStudentAndSubject(Long studentId, Long subjectId, LocalDate fromDate, LocalDate toDate) {
        if (studentId != null) {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
            // Touch to ensure the student exists; name isn't required in response yet.
            student.getId();
        }

        List<Object[]> rows = attendanceRecordRepository.aggregateByStudentAndSubject(studentId, subjectId, fromDate, toDate);
        List<AttendanceReportResponse.SubjectAttendanceItem> items = new ArrayList<>();
        for (Object[] row : rows) {
            Long sId = (Long) row[0];
            String subjectCode = (String) row[1];
            String subjectName = (String) row[2];
            long total = ((Number) row[3]).longValue();
            long present = row[4] != null ? ((Number) row[4]).longValue() : 0L;
            long absent = row[5] != null ? ((Number) row[5]).longValue() : 0L;
            double pct = total == 0 ? 0.0 : round2((present * 100.0) / total);
            items.add(new AttendanceReportResponse.SubjectAttendanceItem(sId, subjectCode, subjectName, total, present, absent, pct));
        }
        return new AttendanceReportResponse(studentId, fromDate, toDate, items);
    }

    public FeeCollectionReportResponse feeCollectionByMonth(LocalDate fromDate, LocalDate toDate) {
        List<Object[]> rows = feePaymentRepository.monthlyCollections(fromDate, toDate);
        List<FeeCollectionReportResponse.MonthlyCollectionItem> months = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Object[] row : rows) {
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            BigDecimal amount = row[2] != null ? (BigDecimal) row[2] : BigDecimal.ZERO;
            long count = ((Number) row[3]).longValue();
            months.add(new FeeCollectionReportResponse.MonthlyCollectionItem(year, month, amount, count));
            total = total.add(amount);
        }
        return new FeeCollectionReportResponse(fromDate, toDate, total, months);
    }

    public PayrollByDepartmentReportResponse payrollPaidByDepartment(Integer month, Integer year) {
        List<Object[]> rows = payrollRepository.paidPayrollByDepartment(month, year);
        List<PayrollByDepartmentReportResponse.DepartmentPayrollItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Object[] row : rows) {
            Long departmentId = (Long) row[0];
            String departmentName = (String) row[1];
            long employeeCount = ((Number) row[2]).longValue();
            BigDecimal netSum = row[3] != null ? (BigDecimal) row[3] : BigDecimal.ZERO;
            long payrollCount = ((Number) row[4]).longValue();
            items.add(new PayrollByDepartmentReportResponse.DepartmentPayrollItem(departmentId, departmentName, employeeCount, netSum, payrollCount));
            total = total.add(netSum);
        }
        return new PayrollByDepartmentReportResponse(month, year, total, items);
    }

    public LibraryOverdueReportResponse libraryOverdue(LocalDate asOfDate) {
        LocalDate asOf = asOfDate != null ? asOfDate : LocalDate.now();
        List<BookIssue> overdue = bookIssueRepository.findActiveOverdueAsOf(asOf);

        List<LibraryOverdueReportResponse.OverdueItem> items = new ArrayList<>();
        BigDecimal totalFine = BigDecimal.ZERO;
        for (BookIssue issue : overdue) {
            String borrowerType;
            Long borrowerId;
            String borrowerName;
            if (issue.getStudent() != null) {
                borrowerType = "STUDENT";
                borrowerId = issue.getStudent().getId();
                borrowerName = issue.getStudent().getFirstName() + " " + issue.getStudent().getLastName();
            } else if (issue.getEmployee() != null) {
                borrowerType = "EMPLOYEE";
                borrowerId = issue.getEmployee().getId();
                borrowerName = issue.getEmployee().getFirstName() + " " + issue.getEmployee().getLastName();
            } else {
                borrowerType = "UNKNOWN";
                borrowerId = null;
                borrowerName = null;
            }

            long daysOverdue = issue.getDueDate() == null ? 0 : Math.max(0, ChronoUnit.DAYS.between(issue.getDueDate(), asOf));
            BigDecimal fine = issue.getFineAmount() != null ? issue.getFineAmount() : BigDecimal.ZERO;
            totalFine = totalFine.add(fine);

            items.add(new LibraryOverdueReportResponse.OverdueItem(
                    issue.getId(),
                    issue.getBook().getId(),
                    issue.getBook().getTitle(),
                    borrowerType,
                    borrowerId,
                    borrowerName,
                    issue.getIssueDate(),
                    issue.getDueDate(),
                    daysOverdue,
                    fine,
                    issue.getStatus()
            ));
        }

        return new LibraryOverdueReportResponse(asOf, items.size(), totalFine, items);
    }

    public DepartmentPerformanceSummaryResponse departmentPerformance(LocalDate fromDate, LocalDate toDate, Long examId, Long courseId) {
        // Attendance rate by department
        Map<Long, AttendanceAgg> attendanceByDept = new HashMap<>();
        for (Object[] row : attendanceRecordRepository.aggregateByDepartment(fromDate, toDate)) {
            Long deptId = (Long) row[0];
            String deptName = (String) row[1];
            long total = ((Number) row[2]).longValue();
            long present = row[3] != null ? ((Number) row[3]).longValue() : 0L;
            attendanceByDept.put(deptId, new AttendanceAgg(deptName, total, present));
        }

        // Exam performance by department
        Map<Long, DeptPerfAgg> perfByDept = new HashMap<>();
        for (Object[] row : examResultRepository.aggregateDepartmentPerformance(fromDate, toDate, examId, courseId)) {
            Long deptId = (Long) row[0];
            String deptName = (String) row[1];
            long studentCount = ((Number) row[2]).longValue();
            Double avgGp = (Double) row[3];
            long passCount = row[4] != null ? ((Number) row[4]).longValue() : 0L;
            long totalResults = ((Number) row[5]).longValue();
            Double passRate = totalResults == 0 ? null : round2((passCount * 100.0) / totalResults);
            perfByDept.put(deptId, new DeptPerfAgg(deptName, studentCount, avgGp, passRate));
        }

        // Merge (union of keys)
        Map<Long, String> deptNames = new HashMap<>();
        attendanceByDept.forEach((k, v) -> deptNames.put(k, v.departmentName));
        perfByDept.forEach((k, v) -> deptNames.put(k, v.departmentName));

        List<DepartmentPerformanceSummaryResponse.DepartmentPerformanceItem> out = new ArrayList<>();
        deptNames.keySet().stream().sorted().forEach(deptId -> {
            String deptName = deptNames.get(deptId);
            AttendanceAgg att = attendanceByDept.get(deptId);
            DeptPerfAgg perf = perfByDept.get(deptId);

            long studentCount = perf != null ? perf.studentCount : 0L;
            Double avgGp = perf != null ? perf.averageGradePoint : null;
            Double passRate = perf != null ? perf.passRatePercentage : null;
            Double attendanceRate = null;
            if (att != null && att.total > 0) {
                attendanceRate = round2((att.present * 100.0) / att.total);
            }

            out.add(new DepartmentPerformanceSummaryResponse.DepartmentPerformanceItem(
                    deptId,
                    deptName,
                    studentCount,
                    avgGp != null ? round2(avgGp) : null,
                    passRate,
                    attendanceRate
            ));
        });

        return new DepartmentPerformanceSummaryResponse(fromDate, toDate, examId, courseId, out);
    }

    private double round2(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private static final class AttendanceAgg {
        private final String departmentName;
        private final long total;
        private final long present;

        private AttendanceAgg(String departmentName, long total, long present) {
            this.departmentName = departmentName;
            this.total = total;
            this.present = present;
        }
    }

    private static final class DeptPerfAgg {
        private final String departmentName;
        private final long studentCount;
        private final Double averageGradePoint;
        private final Double passRatePercentage;

        private DeptPerfAgg(String departmentName, long studentCount, Double averageGradePoint, Double passRatePercentage) {
            this.departmentName = departmentName;
            this.studentCount = studentCount;
            this.averageGradePoint = averageGradePoint;
            this.passRatePercentage = passRatePercentage;
        }
    }
}


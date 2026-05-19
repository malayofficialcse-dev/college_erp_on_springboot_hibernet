package com.example.demo.service;

import com.example.demo.dto.dashboard.AdminDashboardResponse;
import com.example.demo.dto.dashboard.StudentDashboardResponse;
import com.example.demo.dto.dashboard.TeacherDashboardResponse;
import com.example.demo.model.AttendanceRecord;
import com.example.demo.model.Employee;
import com.example.demo.model.ExamResult;
import com.example.demo.model.FeePayment;
import com.example.demo.model.Notice;
import com.example.demo.model.Student;
import com.example.demo.model.Subject;
import com.example.demo.model.Teacher;
import com.example.demo.model.Timetable;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.BookIssueRepository;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.ExamResultRepository;
import com.example.demo.repository.FeePaymentRepository;
import com.example.demo.repository.LeaveRequestRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.SubjectRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.repository.TimetableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private static final int ANNUAL_LEAVE_LIMIT = 24;

    @Autowired private StudentService studentService;
    @Autowired private TeacherService teacherService;
    @Autowired private NoticeService noticeService;
    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private FeePaymentRepository feePaymentRepository;
    @Autowired private ExamResultRepository examResultRepository;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private TimetableRepository timetableRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private LeaveRequestRepository leaveRequestRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private BookIssueRepository bookIssueRepository;

    public StudentDashboardResponse getStudentDashboard(Long studentId) {
        Student student = studentService.getStudentById(studentId);
        List<AttendanceRecord> attendanceRecords = attendanceRepository.findByStudentId(studentId);
        long totalClasses = attendanceRecords.size();
        long classesAttended = attendanceRecords.stream()
                .filter(record -> "PRESENT".equalsIgnoreCase(record.getStatus()))
                .count();
        double attendancePercentage = totalClasses > 0 ? classesAttended * 100.0 / totalClasses : 0.0;

        List<StudentDashboardResponse.SubjectAttendance> subjectAttendance = attendanceRecords.stream()
                .filter(record -> record.getSubject() != null)
                .collect(Collectors.groupingBy(record -> record.getSubject().getId()))
                .values().stream()
                .map(records -> {
                    AttendanceRecord first = records.get(0);
                    long subjectTotal = records.size();
                    long subjectPresent = records.stream()
                            .filter(record -> "PRESENT".equalsIgnoreCase(record.getStatus()))
                            .count();
                    double subjectPercentage = subjectTotal > 0 ? subjectPresent * 100.0 / subjectTotal : 0.0;
                    return new StudentDashboardResponse.SubjectAttendance(
                            first.getSubject().getId(),
                            first.getSubject().getName(),
                            subjectTotal,
                            subjectPresent,
                            round(subjectPercentage)
                    );
                })
                .sorted(Comparator.comparing(StudentDashboardResponse.SubjectAttendance::subjectName))
                .toList();

        List<FeePayment> recentPayments = feePaymentRepository.findTop5ByStudentIdOrderByPaymentDateDescIdDesc(studentId);
        BigDecimal totalPaid = Optional.ofNullable(feePaymentRepository.sumPaidFeeByStudent(studentId)).orElse(BigDecimal.ZERO);

        List<ExamResult> recentResults = examResultRepository.findTop5ByStudentIdOrderByIdDesc(studentId);
        List<Notice> notices = noticeService.getRelevantNotices("STUDENTS",
                student.getDepartment() != null ? student.getDepartment().getId() : null, 5);

        return new StudentDashboardResponse(
                new StudentDashboardResponse.StudentProfile(
                        student.getId(),
                        student.getEnrollmentNumber(),
                        student.getFirstName() + " " + student.getLastName(),
                        student.getEmail(),
                        student.getPhone(),
                        student.getStatus(),
                        student.getCurrentSemester(),
                        student.getDepartment() != null ? student.getDepartment().getName() : null,
                        student.getCourses().stream().map(course -> course.getTitle()).sorted().toList()
                ),
                new StudentDashboardResponse.AttendanceSummary(
                        totalClasses,
                        classesAttended,
                        round(attendancePercentage),
                        subjectAttendance
                ),
                new StudentDashboardResponse.FeeSummary(
                        totalPaid,
                        feePaymentRepository.countByStudentIdAndStatusIgnoreCase(studentId, "PENDING"),
                        feePaymentRepository.countByStudentIdAndStatusIgnoreCase(studentId, "OVERDUE"),
                        recentPayments.stream()
                                .map(payment -> new StudentDashboardResponse.PaymentSummary(
                                        payment.getId(),
                                        payment.getNetAmount(),
                                        payment.getStatus(),
                                        payment.getSemester(),
                                        payment.getPaymentDate(),
                                        payment.getReceiptNumber()
                                ))
                                .toList()
                ),
                recentResults.stream()
                        .map(result -> new StudentDashboardResponse.ResultSummary(
                                result.getId(),
                                result.getExam() != null ? result.getExam().getExamName() : null,
                                result.getSubject() != null ? result.getSubject().getName() : null,
                                result.getMarksObtained(),
                                result.getTotalMarks(),
                                result.getGrade(),
                                result.getResultStatus(),
                                result.getSemester()
                        ))
                        .toList(),
                notices.stream()
                        .map(this::toStudentNoticeSummary)
                        .toList()
        );
    }

    public TeacherDashboardResponse getTeacherDashboard(Long teacherId) {
        Teacher teacher = teacherService.getTeacherById(teacherId);
        String dayOfWeek = DayOfWeek.from(LocalDate.now()).name();
        List<Timetable> timetableToday = timetableRepository.findByTeacherIdAndDayOfWeek(teacherId, dayOfWeek);
        List<Subject> assignedSubjects = subjectRepository.findByTeacherId(teacherId);
        long markedTimetables = attendanceRepository.countDistinctTimetablesMarkedByTeacherOnDate(teacherId, LocalDate.now());
        int pendingAttendance = (int) Math.max(0, timetableToday.size() - markedTimetables);

        Employee linkedEmployee = employeeRepository.findByEmployeeCode(teacher.getEmployeeCode()).orElse(null);
        LocalDate periodStart = LocalDate.now().withDayOfYear(1);
        LocalDate periodEnd = LocalDate.now().withMonth(12).withDayOfMonth(31);
        int approvedLeaveDays = linkedEmployee != null
                ? Optional.ofNullable(leaveRequestRepository.sumApprovedDaysByEmployeeAndDateRange(linkedEmployee.getId(), periodStart, periodEnd)).orElse(0)
                : 0;

        return new TeacherDashboardResponse(
                new TeacherDashboardResponse.TeacherProfile(
                        teacher.getId(),
                        teacher.getEmployeeCode(),
                        teacher.getFirstName() + " " + teacher.getLastName(),
                        teacher.getEmail(),
                        teacher.getDesignation(),
                        teacher.getQualification(),
                        teacher.getDepartment() != null ? teacher.getDepartment().getName() : null,
                        teacher.getStatus()
                ),
                assignedSubjects.stream()
                        .map(subject -> new TeacherDashboardResponse.SubjectSummary(
                                subject.getId(),
                                subject.getSubjectCode(),
                                subject.getName(),
                                subject.getSemesterNumber(),
                                subject.getSubjectType(),
                                subject.getCourse() != null ? subject.getCourse().getTitle() : null
                        ))
                        .sorted(Comparator.comparing(TeacherDashboardResponse.SubjectSummary::name))
                        .toList(),
                timetableToday.stream()
                        .map(this::toClassSummary)
                        .sorted(Comparator.comparing(TeacherDashboardResponse.ClassSummary::startTime))
                        .toList(),
                pendingAttendance,
                new TeacherDashboardResponse.LeaveBalance(
                        ANNUAL_LEAVE_LIMIT,
                        approvedLeaveDays,
                        Math.max(0, ANNUAL_LEAVE_LIMIT - approvedLeaveDays),
                        periodStart,
                        periodEnd
                ),
                timetableToday.stream()
                        .map(this::toClassSummary)
                        .sorted(Comparator.comparing(TeacherDashboardResponse.ClassSummary::startTime))
                        .toList()
        );
    }

    public AdminDashboardResponse getAdminDashboard() {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today.withDayOfMonth(today.lengthOfMonth());
        return new AdminDashboardResponse(
                studentRepository.count(),
                employeeRepository.count(),
                departmentRepository.count(),
                bookIssueRepository.countOverdueIssues(today),
                leaveRequestRepository.countByStatusIgnoreCase("PENDING"),
                Optional.ofNullable(feePaymentRepository.sumTotalCollected()).orElse(BigDecimal.ZERO),
                Optional.ofNullable(feePaymentRepository.sumCollectedBetween(monthStart, monthEnd)).orElse(BigDecimal.ZERO),
                today
        );
    }

    private StudentDashboardResponse.NoticeSummary toStudentNoticeSummary(Notice notice) {
        return new StudentDashboardResponse.NoticeSummary(
                notice.getId(),
                notice.getTitle(),
                notice.getNoticeType(),
                notice.getTargetAudience(),
                notice.getDepartment() != null ? notice.getDepartment().getName() : null,
                notice.getPublishedAt(),
                notice.getExpiryDate()
        );
    }

    private TeacherDashboardResponse.ClassSummary toClassSummary(Timetable timetable) {
        return new TeacherDashboardResponse.ClassSummary(
                timetable.getId(),
                timetable.getDayOfWeek(),
                timetable.getStartTime(),
                timetable.getEndTime(),
                timetable.getRoomNumber(),
                timetable.getClassType(),
                timetable.getSubject() != null ? timetable.getSubject().getName() : null,
                timetable.getCourse() != null ? timetable.getCourse().getTitle() : null,
                timetable.getSemester() != null ? timetable.getSemester().getSemesterName() : null
        );
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

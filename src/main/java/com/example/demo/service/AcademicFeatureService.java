package com.example.demo.service;

import com.example.demo.dto.academic.AcademicPerformanceResponse;
import com.example.demo.dto.academic.AttendanceAnalyticsResponse;
import com.example.demo.dto.academic.HallTicketResponse;
import com.example.demo.dto.academic.PromotionResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.AttendanceRecord;
import com.example.demo.model.Course;
import com.example.demo.model.ExamResult;
import com.example.demo.model.ExamSchedule;
import com.example.demo.model.Student;
import com.example.demo.model.StudentSubjectEnrollment;
import com.example.demo.model.Subject;
import com.example.demo.repository.AttendanceRepository;
import com.example.demo.repository.ExamResultRepository;
import com.example.demo.repository.ExamScheduleRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.StudentSubjectEnrollmentRepository;
import com.example.demo.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AcademicFeatureService {

    private static final double SHORTAGE_THRESHOLD = 75.0;
    private static final double PROMOTION_MIN_GPA = 5.0;

    @Autowired private StudentService studentService;
    @Autowired private StudentRepository studentRepository;
    @Autowired private ExamResultRepository examResultRepository;
    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private ExamScheduleRepository examScheduleRepository;
    @Autowired private StudentSubjectEnrollmentRepository enrollmentRepository;

    public AcademicPerformanceResponse getPerformance(Long studentId) {
        Student student = studentService.getStudentById(studentId);
        List<ExamResult> allResults = examResultRepository.findByStudentIdOrderBySemesterAscIdAsc(studentId);
        List<String> semesters = examResultRepository.findDistinctSemestersByStudentId(studentId);

        List<AcademicPerformanceResponse.SemesterPerformance> semesterPerformances = semesters.stream()
                .map(semester -> buildSemesterPerformance(semester, examResultRepository.findByStudentIdAndSemesterOrderByIdAsc(studentId, semester)))
                .toList();

        double cgpa = calculateWeightedAverage(allResults);
        double currentGpa = semesterPerformances.isEmpty() ? 0.0 : semesterPerformances.get(semesterPerformances.size() - 1).gpa();

        return new AcademicPerformanceResponse(
                student.getId(),
                student.getFirstName() + " " + student.getLastName(),
                round(currentGpa),
                round(cgpa),
                semesterPerformances
        );
    }

    public AttendanceAnalyticsResponse getAttendanceAnalytics(Long studentId) {
        Student student = studentService.getStudentById(studentId);
        List<AttendanceRecord> records = attendanceRepository.findByStudentId(studentId);
        long total = records.size();
        long attended = records.stream().filter(this::isPresentLike).count();
        double overallPercentage = total > 0 ? attended * 100.0 / total : 0.0;

        List<AttendanceAnalyticsResponse.SubjectAttendanceAnalytics> bySubject = records.stream()
                .filter(record -> record.getSubject() != null)
                .collect(Collectors.groupingBy(record -> record.getSubject().getId(), LinkedHashMap::new, Collectors.toList()))
                .values().stream()
                .map(subjectRecords -> {
                    Subject subject = subjectRecords.get(0).getSubject();
                    long subjectTotal = subjectRecords.size();
                    long subjectAttended = subjectRecords.stream().filter(this::isPresentLike).count();
                    return new AttendanceAnalyticsResponse.SubjectAttendanceAnalytics(
                            subject.getId(),
                            subject.getName(),
                            subjectAttended,
                            subjectTotal,
                            round(percentage(subjectAttended, subjectTotal))
                    );
                })
                .sorted(Comparator.comparing(AttendanceAnalyticsResponse.SubjectAttendanceAnalytics::subjectName))
                .toList();

        List<AttendanceAnalyticsResponse.MonthlyAttendanceTrend> monthlyTrends = records.stream()
                .collect(Collectors.groupingBy(record -> record.getDate().getMonth(), LinkedHashMap::new, Collectors.toList()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    long monthTotal = entry.getValue().size();
                    long monthAttended = entry.getValue().stream().filter(this::isPresentLike).count();
                    return new AttendanceAnalyticsResponse.MonthlyAttendanceTrend(
                            entry.getKey().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                            monthAttended,
                            monthTotal,
                            round(percentage(monthAttended, monthTotal))
                    );
                })
                .toList();

        List<AttendanceAnalyticsResponse.ShortageItem> shortages = bySubject.stream()
                .filter(item -> item.percentage() < SHORTAGE_THRESHOLD)
                .map(item -> new AttendanceAnalyticsResponse.ShortageItem(
                        item.subjectId(),
                        item.subjectName(),
                        item.percentage(),
                        SHORTAGE_THRESHOLD
                ))
                .toList();

        return new AttendanceAnalyticsResponse(
                studentId,
                student.getFirstName() + " " + student.getLastName(),
                round(overallPercentage),
                bySubject,
                monthlyTrends,
                shortages
        );
    }

    @Transactional
    public PromotionResponse promoteStudent(Long studentId) {
        Student student = studentService.getStudentById(studentId);
        Integer currentSemester = Optional.ofNullable(student.getCurrentSemester()).orElse(1);
        int maxSemesters = getMaxSemesters(student);
        List<ExamResult> relevantResults = examResultRepository.findByStudentIdOrderBySemesterAscIdAsc(studentId).stream()
                .filter(result -> result.getSubject() != null && Objects.equals(result.getSubject().getSemesterNumber(), currentSemester))
                .toList();

        if (relevantResults.isEmpty()) {
            throw new IllegalStateException("No exam results found for current semester " + currentSemester + ".");
        }

        double semesterGpa = calculateWeightedAverage(relevantResults);
        boolean allPassed = relevantResults.stream().allMatch(this::isPass);

        if (!allPassed || semesterGpa < PROMOTION_MIN_GPA) {
            return new PromotionResponse(
                    student.getId(),
                    student.getFirstName() + " " + student.getLastName(),
                    currentSemester,
                    currentSemester,
                    student.getStatus(),
                    round(calculateWeightedAverage(examResultRepository.findByStudentIdOrderBySemesterAscIdAsc(studentId))),
                    false,
                    "Student does not meet promotion criteria for semester " + currentSemester + "."
            );
        }

        if (currentSemester >= maxSemesters) {
            student.setStatus("GRADUATED");
            studentRepository.save(student);
            return new PromotionResponse(
                    student.getId(),
                    student.getFirstName() + " " + student.getLastName(),
                    currentSemester,
                    currentSemester,
                    student.getStatus(),
                    round(calculateWeightedAverage(examResultRepository.findByStudentIdOrderBySemesterAscIdAsc(studentId))),
                    true,
                    "Student has completed the final semester and has been marked as GRADUATED."
            );
        }

        student.setCurrentSemester(currentSemester + 1);
        studentRepository.save(student);
        return new PromotionResponse(
                student.getId(),
                student.getFirstName() + " " + student.getLastName(),
                currentSemester,
                currentSemester + 1,
                student.getStatus(),
                round(calculateWeightedAverage(examResultRepository.findByStudentIdOrderBySemesterAscIdAsc(studentId))),
                true,
                "Student promoted successfully."
        );
    }

    @Transactional
    public List<PromotionResponse> promoteStudentsInSemester(Integer semester) {
        return studentRepository.findByCurrentSemesterAndStatusIgnoreCase(semester, "ACTIVE").stream()
                .map(student -> promoteStudent(student.getId()))
                .toList();
    }

    public HallTicketResponse generateHallTicket(Long studentId, Long examId) {
        Student student = studentService.getStudentById(studentId);
        int semesterNumber = Optional.ofNullable(student.getCurrentSemester()).orElseThrow(
                () -> new IllegalStateException("Student current semester is not set.")
        );
        Course primaryCourse = getPrimaryCourse(student);

        List<StudentSubjectEnrollment> enrollments = enrollmentRepository
                .findByStudentIdAndSemesterNumberOrderByIdAsc(studentId, semesterNumber);
        Set<Long> enrolledSubjectIds = enrollments.stream()
                .map(enrollment -> enrollment.getSubject().getId())
                .collect(Collectors.toSet());

        List<ExamSchedule> schedules = examScheduleRepository
                .findByExamCourseIdAndSubjectSemesterNumberOrderByExamDateAscStartTimeAsc(primaryCourse.getId(), semesterNumber)
                .stream()
                .filter(schedule -> examId == null || schedule.getExam().getId().equals(examId))
                .filter(schedule -> enrolledSubjectIds.isEmpty() || enrolledSubjectIds.contains(schedule.getSubject().getId()))
                .toList();

        if (schedules.isEmpty()) {
            throw new ResourceNotFoundException("HallTicket", "studentId", studentId);
        }

        String examName = schedules.get(0).getExam().getExamName();
        String examType = schedules.get(0).getExam().getExamType();

        List<HallTicketResponse.ExamPaper> papers = schedules.stream()
                .map(schedule -> new HallTicketResponse.ExamPaper(
                        schedule.getId(),
                        schedule.getSubject().getSubjectCode(),
                        schedule.getSubject().getName(),
                        schedule.getExamDate(),
                        schedule.getStartTime(),
                        schedule.getEndTime(),
                        schedule.getRoomNumber(),
                        buildSeatNumber(student, schedule)
                ))
                .toList();

        List<String> instructions = schedules.stream()
                .map(ExamSchedule::getInstructions)
                .filter(Objects::nonNull)
                .filter(text -> !text.isBlank())
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
        if (instructions.isEmpty()) {
            instructions.add("Carry your ID card and hall ticket to every examination.");
            instructions.add("Report to the exam room at least 30 minutes before the start time.");
            instructions.add("Electronic gadgets are not permitted unless explicitly approved.");
        }

        return new HallTicketResponse(
                student.getId(),
                student.getFirstName() + " " + student.getLastName(),
                student.getEnrollmentNumber(),
                student.getDepartment() != null ? student.getDepartment().getName() : null,
                primaryCourse.getTitle(),
                semesterNumber,
                examName,
                examType,
                papers,
                instructions
        );
    }

    private AcademicPerformanceResponse.SemesterPerformance buildSemesterPerformance(String semester, List<ExamResult> results) {
        double totalCredits = results.stream().mapToDouble(this::creditsOf).sum();
        double earnedCredits = results.stream().filter(this::isPass).mapToDouble(this::creditsOf).sum();
        double gpa = calculateWeightedAverage(results);

        List<AcademicPerformanceResponse.ResultItem> items = results.stream()
                .map(result -> new AcademicPerformanceResponse.ResultItem(
                        result.getId(),
                        result.getSubject() != null ? result.getSubject().getSubjectCode() : null,
                        result.getSubject() != null ? result.getSubject().getName() : null,
                        result.getSubject() != null ? result.getSubject().getCredits() : null,
                        result.getMarksObtained(),
                        result.getTotalMarks(),
                        result.getGrade(),
                        result.getGradePoint(),
                        result.getResultStatus()
                ))
                .toList();

        return new AcademicPerformanceResponse.SemesterPerformance(
                semester,
                round(gpa),
                round(earnedCredits),
                round(totalCredits),
                items
        );
    }

    private boolean isPresentLike(AttendanceRecord record) {
        return "PRESENT".equalsIgnoreCase(record.getStatus()) || "LATE".equalsIgnoreCase(record.getStatus());
    }

    private boolean isPass(ExamResult result) {
        return "PASS".equalsIgnoreCase(result.getResultStatus());
    }

    private double creditsOf(ExamResult result) {
        if (result.getSubject() == null || result.getSubject().getCredits() == null) {
            return 0.0;
        }
        return result.getSubject().getCredits();
    }

    private double calculateWeightedAverage(Collection<ExamResult> results) {
        double weightedPoints = 0.0;
        double totalCredits = 0.0;
        for (ExamResult result : results) {
            double credits = creditsOf(result);
            double gradePoint = Optional.ofNullable(result.getGradePoint()).orElse(0.0);
            weightedPoints += gradePoint * credits;
            totalCredits += credits;
        }
        return totalCredits > 0 ? weightedPoints / totalCredits : 0.0;
    }

    private double percentage(long attended, long total) {
        return total > 0 ? attended * 100.0 / total : 0.0;
    }

    private int getMaxSemesters(Student student) {
        return student.getCourses().stream()
                .map(Course::getTotalSemesters)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElseThrow(() -> new IllegalStateException("Student is not linked to any course with total semesters."));
    }

    private Course getPrimaryCourse(Student student) {
        return student.getCourses().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Student is not enrolled in any course."));
    }

    private String buildSeatNumber(Student student, ExamSchedule schedule) {
        String prefix = schedule.getSeatNumberPrefix() != null && !schedule.getSeatNumberPrefix().isBlank()
                ? schedule.getSeatNumberPrefix()
                : "SEAT";
        return prefix + "-" + student.getEnrollmentNumber() + "-" + schedule.getId();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

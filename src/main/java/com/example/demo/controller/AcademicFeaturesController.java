package com.example.demo.controller;

import com.example.demo.dto.academic.AcademicPerformanceResponse;
import com.example.demo.dto.academic.AttendanceAnalyticsResponse;
import com.example.demo.dto.academic.HallTicketResponse;
import com.example.demo.dto.academic.PromotionResponse;
import com.example.demo.dto.academic.SubjectEnrollmentRequest;
import com.example.demo.dto.academic.SubjectEnrollmentResponse;
import com.example.demo.model.ExamSchedule;
import com.example.demo.model.Subject;
import com.example.demo.service.AcademicFeatureService;
import com.example.demo.service.ExamScheduleService;
import com.example.demo.service.SubjectEnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/academic-features")
@CrossOrigin(origins = "*")
public class AcademicFeaturesController {

    @Autowired private AcademicFeatureService academicFeatureService;
    @Autowired private ExamScheduleService examScheduleService;
    @Autowired private SubjectEnrollmentService subjectEnrollmentService;

    @GetMapping("/performance/student/{studentId}")
    public ResponseEntity<AcademicPerformanceResponse> getPerformance(@PathVariable Long studentId) {
        return ResponseEntity.ok(academicFeatureService.getPerformance(studentId));
    }

    @GetMapping("/attendance-analytics/student/{studentId}")
    public ResponseEntity<AttendanceAnalyticsResponse> getAttendanceAnalytics(@PathVariable Long studentId) {
        return ResponseEntity.ok(academicFeatureService.getAttendanceAnalytics(studentId));
    }

    @PostMapping("/promotion/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<PromotionResponse> promoteStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(academicFeatureService.promoteStudent(studentId));
    }

    @PostMapping("/promotion/semester/{semester}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<List<PromotionResponse>> promoteSemester(@PathVariable Integer semester) {
        return ResponseEntity.ok(academicFeatureService.promoteStudentsInSemester(semester));
    }

    @GetMapping("/hall-ticket/student/{studentId}")
    public ResponseEntity<HallTicketResponse> getHallTicket(@PathVariable Long studentId,
                                                            @RequestParam(required = false) Long examId) {
        return ResponseEntity.ok(academicFeatureService.generateHallTicket(studentId, examId));
    }

    @GetMapping("/exam-schedules")
    public ResponseEntity<Page<ExamSchedule>> getExamSchedules(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(examScheduleService.getAll(pageable));
    }

    @GetMapping("/exam-schedules/{id}")
    public ResponseEntity<ExamSchedule> getExamSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(examScheduleService.getById(id));
    }

    @GetMapping("/exam-schedules/exam/{examId}")
    public ResponseEntity<Page<ExamSchedule>> getExamSchedulesByExam(@PathVariable Long examId,
                                                                     @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(examScheduleService.getByExam(examId, pageable));
    }

    @GetMapping("/exam-schedules/course/{courseId}/semester/{semesterNumber}")
    public ResponseEntity<Page<ExamSchedule>> getExamSchedulesByCourseSemester(@PathVariable Long courseId,
                                                                               @PathVariable Integer semesterNumber,
                                                                               @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(examScheduleService.getByCourseAndSemester(courseId, semesterNumber, pageable));
    }

    @GetMapping("/exam-schedules/date-range")
    public ResponseEntity<List<ExamSchedule>> getExamSchedulesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(examScheduleService.getByDateRange(from, to));
    }

    @PostMapping("/exam-schedules")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ExamSchedule> createExamSchedule(@RequestBody ExamSchedule schedule) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examScheduleService.create(schedule));
    }

    @PutMapping("/exam-schedules/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ExamSchedule> updateExamSchedule(@PathVariable Long id, @RequestBody ExamSchedule details) {
        return ResponseEntity.ok(examScheduleService.update(id, details));
    }

    @DeleteMapping("/exam-schedules/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Void> deleteExamSchedule(@PathVariable Long id) {
        examScheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/enrollments/student/{studentId}")
    public ResponseEntity<List<SubjectEnrollmentResponse>> getEnrollments(@PathVariable Long studentId,
                                                                          @RequestParam(required = false) Integer semesterNumber) {
        return ResponseEntity.ok(subjectEnrollmentService.getEnrollments(studentId, semesterNumber));
    }

    @GetMapping("/electives/course/{courseId}/semester/{semesterNumber}")
    public ResponseEntity<List<Subject>> getAvailableElectives(@PathVariable Long courseId,
                                                               @PathVariable Integer semesterNumber) {
        return ResponseEntity.ok(subjectEnrollmentService.getAvailableElectives(courseId, semesterNumber));
    }

    @PostMapping("/enrollments")
    public ResponseEntity<List<SubjectEnrollmentResponse>> enrollSubjects(@RequestBody SubjectEnrollmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectEnrollmentService.enrollSubjects(request, false));
    }

    @PostMapping("/electives/select")
    public ResponseEntity<List<SubjectEnrollmentResponse>> selectElectives(@RequestBody SubjectEnrollmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectEnrollmentService.enrollSubjects(request, true));
    }

    @DeleteMapping("/enrollments/{enrollmentId}")
    public ResponseEntity<Void> dropEnrollment(@PathVariable Long enrollmentId) {
        subjectEnrollmentService.dropEnrollment(enrollmentId);
        return ResponseEntity.noContent().build();
    }
}

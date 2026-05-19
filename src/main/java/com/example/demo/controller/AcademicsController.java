package com.example.demo.controller;

import com.example.demo.model.Course;
import com.example.demo.model.Subject;
import com.example.demo.model.Timetable;
import com.example.demo.service.CourseService;
import com.example.demo.service.SubjectService;
import com.example.demo.service.TimetableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/academics")
@CrossOrigin(origins = "*")
public class AcademicsController {

    @Autowired private CourseService courseService;
    @Autowired private SubjectService subjectService;
    @Autowired private TimetableService timetableService;

    // ---- Courses ----
    @GetMapping("/courses")
    public ResponseEntity<Page<Course>> getAllCourses(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(courseService.getAllCourses(pageable));
    }

    @GetMapping("/courses/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/courses/department/{deptId}")
    public ResponseEntity<Page<Course>> getCoursesByDept(@PathVariable Long deptId,
                                                          @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(courseService.getByDepartment(deptId, pageable));
    }

    @PostMapping("/courses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseService.createCourse(course));
    }

    @PutMapping("/courses/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course details) {
        return ResponseEntity.ok(courseService.updateCourse(id, details));
    }

    // ---- Subjects ----
    @GetMapping("/subjects")
    public ResponseEntity<Page<Subject>> getAllSubjects(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(subjectService.getAllSubjects(pageable));
    }

    @GetMapping("/subjects/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Long id) {
        return ResponseEntity.ok(subjectService.getSubjectById(id));
    }

    @GetMapping("/subjects/course/{courseId}")
    public ResponseEntity<Page<Subject>> getSubjectsByCourse(@PathVariable Long courseId,
                                                              @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(subjectService.getByCourse(courseId, pageable));
    }

    @GetMapping("/subjects/course/{courseId}/semester/{semester}")
    public ResponseEntity<Page<Subject>> getSubjectsBySemester(@PathVariable Long courseId,
                                                                @PathVariable Integer semester,
                                                                @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(subjectService.getByCourseAndSemester(courseId, semester, pageable));
    }

    @PostMapping("/subjects")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Subject> createSubject(@RequestBody Subject subject) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subjectService.createSubject(subject));
    }

    // ---- Timetable ----
    @GetMapping("/timetable/course/{courseId}/day/{day}")
    public ResponseEntity<List<Timetable>> getTimetable(@PathVariable Long courseId, @PathVariable String day) {
        return ResponseEntity.ok(timetableService.getByCourseAndDay(courseId, day.toUpperCase()));
    }

    @GetMapping("/timetable/teacher/{teacherId}")
    public ResponseEntity<Page<Timetable>> getTeacherTimetable(@PathVariable Long teacherId,
                                                                @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(timetableService.getByTeacher(teacherId, pageable));
    }

    @PostMapping("/timetable")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<Timetable> createTimetable(@RequestBody Timetable timetable) {
        return ResponseEntity.status(HttpStatus.CREATED).body(timetableService.create(timetable));
    }

    @DeleteMapping("/timetable/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTimetable(@PathVariable Long id) {
        timetableService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

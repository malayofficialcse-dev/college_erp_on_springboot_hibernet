package com.example.demo.controller;

import com.example.demo.model.Student;
import com.example.demo.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired private StudentService studentService;

    @GetMapping
    public ResponseEntity<Page<Student>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(studentService.getAllStudents(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @GetMapping("/enrollment/{enrollmentNumber}")
    public ResponseEntity<Student> getByEnrollment(@PathVariable String enrollmentNumber) {
        return ResponseEntity.ok(studentService.getStudentByEnrollment(enrollmentNumber));
    }

    @GetMapping("/department/{deptId}")
    public ResponseEntity<Page<Student>> getByDepartment(@PathVariable Long deptId,
                                                          @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(studentService.getStudentsByDepartment(deptId, pageable));
    }

    @GetMapping("/semester/{semester}")
    public ResponseEntity<Page<Student>> getBySemester(@PathVariable Integer semester,
                                                        @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(studentService.getStudentsBySemester(semester, pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Student>> getByStatus(@PathVariable String status,
                                                      @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(studentService.getStudentsByStatus(status, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Student>> search(@RequestParam(required = false) Long departmentId,
                                                @RequestParam(required = false) Integer semester,
                                                @RequestParam(required = false) String status,
                                                @RequestParam(required = false) String keyword,
                                                @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(studentService.search(departmentId, semester, status, keyword, pageable));
    }

    @GetMapping("/count/department/{deptId}")
    public ResponseEntity<Map<String, Long>> countByDepartment(@PathVariable Long deptId) {
        return ResponseEntity.ok(Map.of("count", studentService.countByDepartment(deptId)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Student> create(@Valid @RequestBody Student student) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.createStudent(student));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Student> update(@PathVariable Long id, @RequestBody Student details) {
        return ResponseEntity.ok(studentService.updateStudent(id, details));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}

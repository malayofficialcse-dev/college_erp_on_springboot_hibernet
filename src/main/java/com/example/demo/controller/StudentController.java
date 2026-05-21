package com.example.demo.controller;

import com.example.demo.model.Student;
import com.example.demo.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired private StudentService studentService;
    @Autowired private ExportService exportService;

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

    @GetMapping("/export/all")
    @Operation(summary = "Export all students", description = "Export all student records as PDF, Excel, or CSV")
    public ResponseEntity<byte[]> exportAllStudents(
            @RequestParam(defaultValue = "PDF") String format,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer semester) throws Exception {
        
        List<Student> students = studentService.getAllStudents().stream().collect(Collectors.toList());
        
        if (departmentId != null) {
            students = students.stream()
                .filter(s -> s.getDepartment().getId().equals(departmentId))
                .collect(Collectors.toList());
        }
        
        if (semester != null) {
            students = students.stream()
                .filter(s -> s.getSemester().equals(semester))
                .collect(Collectors.toList());
        }

        List<String> headers = Arrays.asList(
            "ID", "Name", "Email", "Roll Number", "Department", "Semester", "Status"
        );

        List<Map<String, Object>> data = students.stream()
            .map(s -> {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("ID", s.getId());
                row.put("Name", s.getUser().getFullName());
                row.put("Email", s.getUser().getEmail());
                row.put("Roll Number", s.getRollNumber());
                row.put("Department", s.getDepartment().getName());
                row.put("Semester", s.getSemester());
                row.put("Status", s.getStatus());
                return row;
            })
            .collect(Collectors.toList());

        ExportRequest request = ExportRequest.builder()
            .format(format)
            .module("Students")
            .title("Student Records Export")
            .includeSummary(true)
            .build();

        byte[] content = exportService.generateExport(request, headers, data);

        String fileName = "Students-Export." + format.toLowerCase();
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=" + fileName)
            .header("Content-Type", getContentType(format))
            .body(content);
    }

    private String getContentType(String format) {
        switch (format.toUpperCase()) {
            case "PDF":
                return "application/pdf";
            case "EXCEL":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "CSV":
                return "text/csv";
            default:
                return "application/octet-stream";
        }
    }
}

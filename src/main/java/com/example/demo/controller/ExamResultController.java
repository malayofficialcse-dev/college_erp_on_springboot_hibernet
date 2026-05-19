package com.example.demo.controller;

import com.example.demo.model.ExamResult;
import com.example.demo.service.ExamResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/results")
@CrossOrigin(origins = "*")
public class ExamResultController {

    @Autowired private ExamResultService examResultService;

    @GetMapping
    public ResponseEntity<Page<ExamResult>> getAll(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(examResultService.getAllResults(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamResult> getById(@PathVariable Long id) {
        return ResponseEntity.ok(examResultService.getById(id));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<Page<ExamResult>> getByStudent(@PathVariable Long studentId,
                                                          @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(examResultService.getByStudent(studentId, pageable));
    }

    @GetMapping("/student/{studentId}/semester/{semester}")
    public ResponseEntity<Page<ExamResult>> getBySemester(@PathVariable Long studentId,
                                                           @PathVariable String semester,
                                                           @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(examResultService.getByStudentAndSemester(studentId, semester, pageable));
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<Page<ExamResult>> getByExam(@PathVariable Long examId,
                                                       @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(examResultService.getByExam(examId, pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ExamResult> create(@RequestBody ExamResult result) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examResultService.create(result));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ExamResult> update(@PathVariable Long id, @RequestBody ExamResult details) {
        return ResponseEntity.ok(examResultService.update(id, details));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        examResultService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

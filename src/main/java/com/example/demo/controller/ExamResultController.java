package com.example.demo.controller;

import com.example.demo.model.ExamResult;
import com.example.demo.service.ExamResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams/results")
public class ExamResultController {

    @Autowired
    private ExamResultService examResultService;

    @GetMapping
    public List<ExamResult> getAllExamResults() {
        return examResultService.getAllExamResults();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamResult> getExamResultById(@PathVariable Long id) {
        ExamResult examResult = examResultService.getExamResultById(id);
        if (examResult != null) {
            return ResponseEntity.ok(examResult);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ExamResult createExamResult(@RequestBody ExamResult examResult) {
        return examResultService.saveExamResult(examResult);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamResult> updateExamResult(@PathVariable Long id, @RequestBody ExamResult examResultDetails) {
        ExamResult updatedExamResult = examResultService.updateExamResult(id, examResultDetails);
        if (updatedExamResult != null) {
            return ResponseEntity.ok(updatedExamResult);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExamResult(@PathVariable Long id) {
        examResultService.deleteExamResult(id);
        return ResponseEntity.ok().build();
    }
}
